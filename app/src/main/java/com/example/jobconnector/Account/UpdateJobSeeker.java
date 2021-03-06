package com.example.jobconnector.Account;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.jobconnector.Start.MainActivity;
import com.example.jobconnector.R;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.example.jobconnector.Start.MainActivity.username;

public class UpdateJobSeeker extends AppCompatActivity {

    private EditText fullName;
    private EditText dateOfBirth;
    private EditText yearExpr;
    private RadioGroup radioGroup;
    private EditText fields;
    private EditText currentPos;
    private Button update;
    private ImageView cal;
    private AppCompatButton browse;
    private ImageView imgView;
    private Bitmap bitmap;
    String encodeImageString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_job_seeker);

        mapping();

        getData();

        cal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int date = calendar.get(Calendar.DATE);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");

                DatePickerDialog datePickerDialog = new DatePickerDialog(UpdateJobSeeker.this, (datePicker, i, i1, i2) -> {
                    calendar.set(i, i1, i2);
                    dateOfBirth.setText(simpleDateFormat.format(calendar.getTime()));
                }, year, month, date);
                datePickerDialog.show();

            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String date = dateOfBirth.getText().toString();
                if (!date.equals("")) {
                    if (checkDateOfBirth()) {
                        String[] time = dateOfBirth.getText().toString().split("-");
                        date = String.format("%s-%s-%s", time[2], time[1], time[0]);
                        String name = fullName.getText().toString();
                        String yearExp = yearExpr.getText().toString();
                        if (!isNumeric(yearExp)) {
                            yearExp = "";
                        }
                        String field = fields.getText().toString();
                        String currentPosi = currentPos.getText().toString();

                        int genderInt = radioGroup.getCheckedRadioButtonId();
                        RadioButton gender_select = radioGroup.findViewById(genderInt);

                        String gender = "";
                        if (gender_select != null) {
                            gender = gender_select.getText().toString();
                        }
                        updatePro(username, name, date, yearExp, field, currentPosi, gender);
                    }
                } else {
                    date = " ";
                    String name = fullName.getText().toString();
                    String yearExp = yearExpr.getText().toString();
                    if (!isNumeric(yearExp)) {
                        yearExp = "";
                    }
                    String field = fields.getText().toString();
                    String currentPosi = currentPos.getText().toString();

                    int genderInt = radioGroup.getCheckedRadioButtonId();
                    RadioButton gender_select = radioGroup.findViewById(genderInt);

                    String gender = "";
                    if (gender_select != null) {
                        gender = gender_select.getText().toString();
                    }
                    updatePro(username, name, date, yearExp, field, currentPosi, gender);
                }
            }
        });

        browse.setOnClickListener(v -> Dexter.withActivity(UpdateJobSeeker.this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        startActivityForResult(Intent.createChooser(intent, "Browse Image"), 1);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        if(requestCode==1 && resultCode== Activity.RESULT_OK)
        {
            Uri filepath=data.getData();
            try
            {
                InputStream inputStream= getContentResolver().openInputStream(filepath);
                bitmap = BitmapFactory.decodeStream(inputStream);
                imgView.setImageBitmap(bitmap);
                encodeBitmapImage(bitmap);
            }catch (Exception ex)
            {

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void encodeBitmapImage(Bitmap bitmap)
    {
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] bytesofimage =byteArrayOutputStream.toByteArray();
        encodeImageString = android.util.Base64.encodeToString(bytesofimage, Base64.DEFAULT);
        System.out.println(encodeImageString == null);
    }

    public static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean checkDateOfBirth() {
        String[] time = dateOfBirth.getText().toString().split("-");
        if (time.length != 3) {
            Toast.makeText(UpdateJobSeeker.this, "Wrong format of date of birth, correct format is dd-mm-yyyy", Toast.LENGTH_LONG).show();
            return false;
        } else {
            if (Integer.parseInt(time[2]) > Calendar.getInstance().get(Calendar.YEAR)) {
                Toast.makeText(UpdateJobSeeker.this, "Year is invalid", Toast.LENGTH_LONG).show();
                return false;
            } else if (Integer.parseInt(time[2]) == Calendar.getInstance().get(Calendar.YEAR)
                    && Integer.parseInt(time[1]) - 1 > Calendar.getInstance().get(Calendar.MONTH)) {
                Toast.makeText(UpdateJobSeeker.this, "Month is invalid", Toast.LENGTH_LONG).show();
                return false;
            } else if (Integer.parseInt(time[2]) == Calendar.getInstance().get(Calendar.YEAR)
                    && Integer.parseInt(time[1]) - 1 == Calendar.getInstance().get(Calendar.MONTH)
                    && Integer.parseInt(time[0]) > Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) {
                Toast.makeText(UpdateJobSeeker.this, "Date is invalid", Toast.LENGTH_LONG).show();
                return false;
            } else {
                return true;
            }
        }
    }

    private void mapping() {
        fullName = findViewById(R.id.yourFullName);
        dateOfBirth = findViewById(R.id.dateOfBirth);
        radioGroup = findViewById(R.id.radioGroup);
        yearExpr = findViewById(R.id.yearExperience);
        fields = findViewById(R.id.yourFields);
        currentPos = findViewById(R.id.currentPosi);
        update = findViewById(R.id.updatePro);
        cal = findViewById(R.id.calendar);
        browse = findViewById(R.id.browseBtn);
        imgView = findViewById(R.id.jobImage);
    }

    private void updatePro(String username, String name, String date, String yearExp, String field, String currentPosi, String gender) {
        String url = getString(R.string.domain) + "/loginregister/updateJobSeeker.php";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    if (response.equals("Update successfully")) {
                        Toast.makeText(UpdateJobSeeker.this, response, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(UpdateJobSeeker.this, AccountJobSeeker.class)
                                .putExtra("username", MainActivity.username)
                                .putExtra("message",""));
                        finish();
                    } else {
                        Toast.makeText(UpdateJobSeeker.this, response, Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(UpdateJobSeeker.this, error.toString(), Toast.LENGTH_LONG).show();
                }) {
            @NonNull
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> param = new HashMap<>();
                param.put("username", username);
                param.put("name", name);
                param.put("date", date);
                param.put("yearExp", yearExp);
                param.put("field", field);
                param.put("currentPosi", currentPosi);
                param.put("gender", gender);
                if (encodeImageString == null) {
                    URL url;
                    InputStream inputStream = null;
                    try {
                        url = new URL(getString(R.string.domain) + "/image_storage/images/sample_feed.jpg");
                        inputStream = url.openStream();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Bitmap bm = BitmapFactory.decodeStream(inputStream);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] b = baos.toByteArray();
                    encodeImageString = android.util.Base64.encodeToString(b, Base64.DEFAULT);
                }
                param.put("image_upload",encodeImageString);
                return param;
            }
        };

        requestQueue.add(request);
    }

    private void getData() {
        String url = getString(R.string.domain) + "/loginregister/getInfoJobSeeker.php";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        JSONObject object = jsonArray.getJSONObject(0);
                        String name = object.getString("fullname");
                        String yearExperience = object.getString("year_experience");
                        String Fields = object.getString("fields");
                        String currentPosition = object.getString("current_position");
                        String date = object.getString("date_of_birth");
                        String[] time = date.split("-");
                        String gender = object.getString("gender");

                        if (!name.equals("null")) fullName.setText(name);
                        if (!yearExperience.equals("null")) yearExpr.setText(yearExperience);
                        if (!Fields.equals("null")) fields.setText(Fields);
                        if (!currentPosition.equals("null")) currentPos.setText(currentPosition);
                        if (!gender.equals("") && !gender.equals("null")) {
                            RadioButton male = radioGroup.findViewById(R.id.male);
                            RadioButton female = radioGroup.findViewById(R.id.female);
                            if (male.getText().toString().equals(gender)) {
                                male.setChecked(true);
                            } else {
                                female.setChecked(true);
                            }
                        }
                        if (time.length == 3) {
                            dateOfBirth.setText(String.format("%s-%s-%s", time[2], time[1], time[0]));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(UpdateJobSeeker.this, error.toString(), Toast.LENGTH_LONG).show();
                }) {
            @NonNull
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> param = new HashMap<>();
                param.put("username", username);
                return param;
            }
        };

        requestQueue.add(request);
    }

}