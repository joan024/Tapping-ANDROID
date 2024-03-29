package com.example.tappingandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.tappingandroid.Adapter.XatAdapter;
import com.example.tappingandroid.Conexio.ConexioBD;
import com.example.tappingandroid.Dades.Missatge;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Xat extends AppCompatActivity {

    //Es defineixen variables privades per a cadascun dels elements de la interfície d'usuari que s'utilitzaran a l'activitat
    private ListView xatView;
    private EditText etMissatge;
    private Button btnEnviar;
    private ImageView iv_tornar;
    private XatAdapter xatAdapter;
    private int userId = 2,id;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        id = Utilitats.agafarIdShared(this);

        // Obtenir referencies als elements de l'interfaç d'usuario
        xatView = findViewById(R.id.xatListView);
        etMissatge = findViewById(R.id.et_missatge);
        btnEnviar = findViewById(R.id.btn_enviar);
        iv_tornar = findViewById(R.id.iv_tornar);

        iv_tornar.setOnClickListener(v -> onBackPressed());

        // Es defineix un objecte de tipus XatAdapter per gestionar l'adaptador personalitzat
        xatAdapter = new XatAdapter(this, R.layout.misatge_item, new ArrayList<>());
        xatView.setAdapter(xatAdapter);

        // Obtindre els misatges de la base de dades
        List<Missatge> missatges = getMissatgesFromDatabase();

        // Mostrar els misatges en el xat
        xatAdapter.addAll(missatges);

        // Configurar el botó d'enviament de missatges per afegir missatges nous a l'adaptador
        btnEnviar.setOnClickListener(view -> {
            String missatge = etMissatge.getText().toString();
            String nom = "Jo";
            String time = getCurrentTime();
            Missatge newMessage = new Missatge(nom, missatge, time);

            // Insertar el nou missatge a la base de dades
            insertMissatgeToDatabase(newMessage);

            xatAdapter.add(newMessage);
            etMissatge.setText("");
        });

        xatView.post(() -> {
            //Mostrar els ultims misatges
            xatView.smoothScrollToPosition(xatAdapter.getCount() - 1);
        });

    }

    // Funció que obté l'id del xat a partir de la id de l'empresa
    private int getIdXat() throws SQLException {
        Connection conn = ConexioBD.connectar();
        int id_xat = 0;
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM xat WHERE id_empresa = " + id);

            while (rs.next()) {
                id_xat = rs.getInt("id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id_xat;
    }

    // Funció que obté els missatges de la base de dades
    private List<Missatge> getMissatgesFromDatabase() {
        List<Missatge> missatges = new ArrayList<>();
        Connection conn = ConexioBD.connectar();

        try {
            int id_xat = getIdXat();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM lineaxat WHERE id_xat = "+id_xat);

            while (rs.next()) {
                String name= rs.getInt("usuari") == 1 ? "Tapping" : "Jo"; // Si el usuari de la BD es 1 es fica "Tapping" si no es fica "Jo"
                String message = rs.getString("missatge");
                String time = rs.getString("temps");
                Missatge missatge = new Missatge(name, message, time);
                missatges.add(missatge);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return missatges;
    }

    // Funció que insereix un missatge a la base de dades
    private void insertMissatgeToDatabase(Missatge missatge) {
        Connection conn = ConexioBD.connectar();

        try {
            int id_xat = getIdXat();
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO lineaxat (id_xat, usuari, missatge, temps) VALUES (?, ?, ?, ?)");
            pstmt.setInt(1, id_xat);
            pstmt.setInt(2, userId);
            pstmt.setString(3, missatge.getMissatge());
            pstmt.setString(4, missatge.getHora());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Funció que retorna l'hora actual en format de cadena de text
    private String getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
}

