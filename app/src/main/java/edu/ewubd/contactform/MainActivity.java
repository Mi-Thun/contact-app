package edu.ewubd.contactform;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import android.graphics.Bitmap;
import android.util.Base64;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.NameValuePair;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.message.BasicNameValuePair;

import java.io.ByteArrayOutputStream;


public class MainActivity extends Activity {
    private static final int GALLERY_REQUEST_CODE = 123;
    private EditText etName, etEmail, etPhoneHome, etPhoneOffice;
    private ImageView ivPhoto;
    private ContactsDB contactsDB;
    private String image = "";
    private String contactID = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contactsDB = new ContactsDB(this);

        etName = findViewById(R.id.etName);
        etEmail= findViewById(R.id.etEmail);
        etPhoneHome = findViewById(R.id.etPhoneHome);
        etPhoneOffice = findViewById(R.id.etPhoneOffice);
        ivPhoto = findViewById(R.id.ivPhoto);

        ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, GALLERY_REQUEST_CODE);
            }
        });

        findViewById(R.id.btnSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString();
                String email = etEmail.getText().toString();
                String phone_home = etPhoneHome.getText().toString();
                String phone_office = etPhoneOffice.getText().toString();

                String err = "";

                if(!name.isEmpty() && !email.isEmpty() && !phone_home.isEmpty()){
                    if(name.length() < 4 || name.length() > 12 || !name.matches("^[a-zA-Z ]+$")){
                        err += "Invalid Name (4-12 long and only alphabets)\n";
                    }

                    String EMAIL_REGEX = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
                    if(!email.matches(EMAIL_REGEX)){
                        err += "Invalid email format\n";
                    }

                    Pattern pattern = Pattern.compile("^\\+\\d{13}$");
                    if(!phone_home.isEmpty()){
                        Matcher matcher = pattern.matcher(phone_home);
                        if(!matcher.matches()){
                            err += "Invalid phone number (format +88015********)\n";
                        }
                    }

                    if(!phone_office.isEmpty()) {
                        Matcher matcher = pattern.matcher(phone_office);
                        if(!matcher.matches()){
                            err += "Invalid phone number (format +88015********)\n";
                        }
                    }

                } else{
                    err += "Fill all the fields\n";
                }

                if(err.length() > 0){
                    showErrorDialog(err);
                }else{
                    String contactID = name + System.currentTimeMillis();
                    contactsDB.insertContact(contactID, name, email, phone_home, phone_office, MainActivity.this.image);
                    Toast.makeText(MainActivity.this, "Contact Added successfully!", Toast.LENGTH_SHORT).show();
                    // Clear input fields after saving
                    etName.getText().clear();
                    etEmail.getText().clear();
                    etPhoneHome.getText().clear();
                    etPhoneOffice.getText().clear();
                    ivPhoto.setImageResource(R.drawable.baseline_contacts_24);
                }

                String keys[] = {"action", "sid", "semester", "id", "name", "email", "phone_home", "phone_office", "image"};
                String values[] = {"backup", "2019-3-60-046", "2023-2", contactID, name, email, phone_home, phone_office, image};
                httpRequest(keys, values);
            }
        });

        findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            ivPhoto.setImageURI(selectedImageUri);
            try {
                image = convertImageToBase64(selectedImageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String convertImageToBase64(Uri imageUri) throws IOException {
        InputStream imageStream = getContentResolver().openInputStream(imageUri);
        Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private void showErrorDialog(String errMsg){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setMessage(errMsg);
        builder.setCancelable(true);
        builder.setPositiveButton("Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void httpRequest(final String keys[],final String values[]){
        new AsyncTask<Void,Void,String>(){
            @Override
            protected String doInBackground(Void... voids) {
                List<NameValuePair> params=new ArrayList<NameValuePair>();
                for (int i=0; i<keys.length; i++){
                    params.add(new BasicNameValuePair(keys[i],values[i]));
                }
                String url= "http://localhost/events/";
                String data="";
                try {
                    data=JSONParser.getInstance().makeHttpRequest(url,"POST",params);
                    System.out.println(data);
                    return data;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
            protected void onPostExecute(String data){
                if(data!=null){
                    System.out.println(data);
                    System.out.println("Ok2");
                    Toast.makeText(getApplicationContext(),data,Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }
}


