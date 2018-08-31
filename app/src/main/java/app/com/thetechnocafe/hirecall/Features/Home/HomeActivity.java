package app.com.thetechnocafe.hirecall.Features.Home;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import app.com.thetechnocafe.hirecall.Data.FirebaseAuthDB;
import app.com.thetechnocafe.hirecall.Data.FirebaseDB;
import app.com.thetechnocafe.hirecall.Features.Dashboard.DashboardActivity;
import app.com.thetechnocafe.hirecall.Features.Jobs.JobActivity;
import app.com.thetechnocafe.hirecall.R;
import app.com.thetechnocafe.hirecall.Utilities.Constants;
import app.com.thetechnocafe.hirecall.Utilities.PhoneCallContactUtility;
import app.com.thetechnocafe.hirecall.Utilities.SharedPreferencesUtility;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity {

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;
    @BindView(R.id.name_text_view)
    TextView mNameTextView;
    @BindView(R.id.email_text_view)
    TextView mEmailTextView;
    @BindView(R.id.jobs_linear_layout)
    LinearLayout mJobsLinearLayout;
    @BindView(R.id.dashboard_linear_layout)
    LinearLayout mDashboardLinearLayout;
    @BindView(R.id.user_circle_image_view)
    CircleImageView mUserCircleImageView;

    private static final int RC_READ_CONTACTS_PERMISSION = 1;
    private static final int RC_GET_IMAGE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ButterKnife.bind(this);
        PhoneCallContactUtility.getInstance().prepareContactsMap(this);

        initViews();
        setNameAndEmail();
    }

    private void initViews() {
        //Configure toolbar
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_drawer);
        }

        ViewPagerStateAdapter adapter = new ViewPagerStateAdapter(getSupportFragmentManager(), getResources().getStringArray(R.array.home_options));
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);

        mJobsLinearLayout.setOnClickListener(view -> {
            Intent intent = new Intent(getApplication(), JobActivity.class);
            startActivity(intent);
        });

        mDashboardLinearLayout.setOnClickListener(view -> {
            Intent intent = new Intent(getApplication(), DashboardActivity.class);
            startActivity(intent);
        });

        mUserCircleImageView.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Image"), RC_GET_IMAGE);
        });

        checkForContactPermission();
        loadUserImageFromFirebase();
    }

    private void checkForContactPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.READ_CONTACTS}, RC_READ_CONTACTS_PERMISSION);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void setNameAndEmail() {
        String name = SharedPreferencesUtility.getInstance().getUserName(this);
        String email = SharedPreferencesUtility.getInstance().getEmail(this);

        mNameTextView.setText(name);
        mEmailTextView.setText(email);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RC_GET_IMAGE: {
                if (resultCode == RESULT_OK) {
                    //Check if the data is not null and user has selected an image
                    if (data != null) {
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(data.getData());
                            Bitmap selectedImage = BitmapFactory.decodeStream(inputStream);
                            mUserCircleImageView.setImageBitmap(selectedImage);

                            uploadImageAndAddToFirebase(selectedImage);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            }
        }
    }

    private void loadUserImageFromFirebase() {
        String userID = FirebaseAuthDB.getInstance().getSignedInUserUID();

        FirebaseDB.getInstance()
                .getUserImageURL(userID)
                .subscribe(imageUrl -> {
                    if (imageUrl.length() > 0) {
                        Picasso.with(this)
                                .load(imageUrl)
                                .placeholder(R.drawable.ic_user)
                                .into(mUserCircleImageView);
                    }
                });
    }

    @SuppressWarnings("VisibleForTests")
    private void uploadImageAndAddToFirebase(Bitmap imageBitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();

        String userID = FirebaseAuthDB.getInstance().getSignedInUserUID();

        //Get the storage reference
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(Constants.FB_IMAGES + "/" + userID + ".jpeg");
        storageReference.putBytes(data)
                .addOnSuccessListener(taskSnapshot -> {
                    FirebaseDB.getInstance()
                            .setUserImageURL(taskSnapshot.getDownloadUrl().toString())
                            .subscribe();

                    Toast.makeText(getApplicationContext(), "Upload successful", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Unable to upload image, please try again", Toast.LENGTH_SHORT).show());
    }

}
