package com.example.tappingandroid.GestioDeRegistres;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.example.tappingandroid.Inici;
import com.example.tappingandroid.R;

public class IniciSessio extends AppCompatActivity {

    @BindView(R.id.et_usuari)
    EditText usuari;

    @BindView(R.id.et_password)
    EditText password;

    @BindView(R.id.btn_login)
    Button login;

    @BindView(R.id.btn_registre)
    Button registre;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inici_sessio);
        ButterKnife.bind(this);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validarFormulari()) {
                    startIniciActivity();
                }
            }
        });

        registre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRegistreActivity();
            }
        });
    }

    private boolean validarFormulari() {
        String sUsuari = usuari.getText().toString();
        String sPassword = password.getText().toString();

        if (TextUtils.isEmpty(sUsuari)) {
            usuari.setError("Ha d'introduir un usuari");
            return false;
        }

        if (TextUtils.isEmpty(sPassword)) {
            password.setError("Ha d'introduir una contrasenya");
            return false;
        }

        return true;
    }

    private void startIniciActivity() {
        Intent intent = new Intent(IniciSessio.this, Inici.class);
        intent.putExtra("usuari", usuari.getText().toString());
        startActivity(intent);
    }

    private void startRegistreActivity() {
        Intent intent = new Intent(IniciSessio.this, Registre.class);
        startActivity(intent);
    }
}
