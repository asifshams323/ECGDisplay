package com.example.ecgdisplay;

import android.app.Dialog;
import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    public static String ip_address = "192.168.43.199";
    Dialog dialog_insert;
    Button btn_sign_in;
    EditText etxt_email, etxt_password;
    TextView btn_sign_up;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        btn_sign_in = findViewById(R.id.btn_signin);
        btn_sign_up = findViewById(R.id.btn_signup);
        etxt_email = findViewById(R.id.etxt_email);
        etxt_password = findViewById(R.id.etxt_password);
        btn_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Check_Fields();
            }
        });
        btn_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sign_up = new Intent(MainActivity.this, Signup.class);
                startActivity(sign_up);
            }
        });
        showInsertIP();
    }
    private void showInsertIP(){
        dialog_insert = new Dialog(MainActivity.this, R.style.Theme_Dialog);
        dialog_insert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_insert.setContentView(R.layout.custom_getip);
        dialog_insert.setCanceledOnTouchOutside(false);
        final EditText get_ip = dialog_insert.findViewById(R.id.ip_get);
        get_ip.setText("");
        Button save = dialog_insert.findViewById(R.id.save_ip);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(get_ip.getText().length()>0){
                    ip_address = get_ip.getText().toString().trim();
                    dialog_insert.dismiss();
                }
                else{
                    Toast.makeText(MainActivity.this,"Please Enter Valid IP",Toast.LENGTH_LONG).show();
                }

            }
        });
        dialog_insert.show();
    }
    public void Check_Fields(){
        String email_user = etxt_email.getText().toString();
        String password_user = etxt_password.getText().toString();
        Pattern pattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
        Matcher check_email = pattern.matcher(email_user);
        if(email_user.length()>0 || password_user.length()>0){

                Login();

        }
        else{
           Toast.makeText(MainActivity.this,"email or password cannot be empty",Toast.LENGTH_LONG).show();
        }
    }
    public void Login(){
        try {
            String mysqlurl = "jdbc:mysql://"+ip_address+":3306/" + "ecg_data";
            Class.forName("com.mysql.jdbc.Driver");
            Connection mysqlcon = DriverManager.getConnection(mysqlurl, "ecg", "ecg");
            Statement stmt = mysqlcon.createStatement();
            String query = "Select * from user where password = '" + etxt_password.getText().toString() + "'";
            ResultSet rs = stmt.executeQuery(query);
            if(rs.next()){
               startActivity(new Intent(MainActivity.this, ECGData.class));
            }
            else{

                Toast.makeText(MainActivity.this,"Username or Password Incorrect",Toast.LENGTH_LONG).show();

            }

        } catch (SQLException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_LONG).show();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
