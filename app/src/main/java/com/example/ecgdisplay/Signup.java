package com.example.ecgdisplay;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.ecgdisplay.MainActivity.ip_address;

public class Signup extends AppCompatActivity {
    Button btn_sign_up;
    EditText etxt_name, etxt_email, etxt_password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        btn_sign_up = findViewById(R.id.btn_signup);
        etxt_email = findViewById(R.id.etxt_email);
        etxt_name = findViewById(R.id.etxt_name);
        etxt_password = findViewById(R.id.etxt_password);
        btn_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Check_Fields();
            }
        });
    }
    public void Check_Fields(){
        String email_user = etxt_email.getText().toString();
        String password_user = etxt_password.getText().toString();
        String name_user = etxt_name.getText().toString();
        Pattern pattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
        if(email_user.length()>0 || password_user.length()>0){

            Sign_up();

        }
        else{
            Toast.makeText(Signup.this,"Please Fill Every Field",Toast.LENGTH_LONG).show();
        }
    }
    public void Sign_up(){
        try {
            String mysqlurl = "jdbc:mysql://"+ip_address+":3306/" + "ecg_data";
            Class.forName("com.mysql.jdbc.Driver");
            Connection mysqlcon = DriverManager.getConnection(mysqlurl, "ecg", "ecg");
            Statement stmt = mysqlcon.createStatement();
            String query = "INSERT INTO `user`(`id`, `name`, `username`, `password`) " +
                    "VALUES " +
                    "(NULL,'"+etxt_name.getText()+"','"+etxt_email.getText()+"','"+etxt_password.getText()+"')";
            stmt.executeUpdate(query);
            etxt_name.setText("");
            etxt_password.setText("");
            etxt_email.setText("");

            Toast.makeText(Signup.this, "You are successfully registered. ", Toast.LENGTH_LONG).show();
        } catch (SQLException e) {
            e.printStackTrace();
            Toast.makeText(Signup.this, e.toString(), Toast.LENGTH_LONG).show();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
