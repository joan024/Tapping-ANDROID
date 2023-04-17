package com.example.tappingandroid;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.MenuItem;

import com.example.tappingandroid.Conexio.ConexioBD;
import com.example.tappingandroid.Dades.Local;
import com.example.tappingandroid.Dades.Opinio;
import com.example.tappingandroid.Dades.Tapa;
import com.example.tappingandroid.GestioDeRegistres.IniciSessio;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Utilitats {

    public static String obtindreElDiaSetmanal() {
        // Obtener el día de la semana actual
        Calendar calendari = Calendar.getInstance();
        int diaSetmana = calendari.get(Calendar.DAY_OF_WEEK);

        // Obtener el nombre del día de la semana
        String[] dayNames = new String[]{
                "diumenge",
                "dilluns",
                "dimarts",
                "dimecres",
                "dijous",
                "divendres",
                "dissabte"
        };
        return dayNames[diaSetmana - 1];
    }

    public static String queryHorari(Statement stmt2, int id_horari) throws SQLException {
        String horari="";
        String sql2 = "SELECT * FROM horari WHERE id=" + id_horari;
        ResultSet rsHorari = stmt2.executeQuery(sql2);
        while (rsHorari.next()) {
            String dia= Utilitats.obtindreElDiaSetmanal();
            horari = rsHorari.getString(dia);
        }
        rsHorari.close();
        return horari;
    }

    public static String queryFotoPrincipal(Statement stmt5, int id_local) throws SQLException {
        String link="";
        String sql5 = "SELECT * FROM foto WHERE id_local=" + id_local;
        ResultSet rsFoto = stmt5.executeQuery(sql5);
        if (rsFoto.next()) {
            link = rsFoto.getString("link");
        }
        rsFoto.close();
        return link;
    }

    public static List<Tapa> queryTapes(Statement stmt3, List<Tapa> tapes, int id_local) throws SQLException {
        tapes = new ArrayList<>();
        String sql3 = "SELECT tapa.nom, local_tapa.personalitzacio, local_tapa.preu FROM tapa INNER JOIN local_tapa ON local_tapa.id_tapa=tapa.id WHERE local_tapa.id_local=" + id_local;
        ResultSet rsTapes = stmt3.executeQuery(sql3);
        while (rsTapes.next()) {
            String nom_tapa = rsTapes.getString("nom");
            String personalitzacio_tapa = rsTapes.getString("personalitzacio");
            double preu = rsTapes.getDouble("preu");

            tapes.add(new Tapa(nom_tapa,preu,personalitzacio_tapa));
        }
        rsTapes.close();
        return tapes;
    }

    public static List<Opinio> queryOpinions(Statement stmt4, List<Opinio> opinions, int id_local) throws SQLException {
        opinions = new ArrayList<>();
        String sql4 = "SELECT * FROM valoracio INNER JOIN usuari ON usuari.id=valoracio.id_usuari WHERE valoracio.id_local=" + id_local;
        ResultSet rsOpinions = stmt4.executeQuery(sql4);
        while (rsOpinions.next()) {
            String nom_valoracio = rsOpinions.getString("nom");
            double puntuacio = rsOpinions.getDouble("puntuacio");
            String comentari = rsOpinions.getString("comentari");
            Date data = rsOpinions.getDate("data");

            Log.d("juliaaa", String.valueOf(puntuacio));
            opinions.add(new Opinio(nom_valoracio,data,comentari,puntuacio));
        }
        rsOpinions.close();
        return opinions;
    }

    public static Double calcularMitjanaPuntuacio(List<Opinio> opinions) {
        double sumaPuntuacions = 0;
        int numOpinions = opinions.size();

        for (Opinio opinio : opinions) {
            sumaPuntuacions += opinio.getPuntuacio();
        }
        return sumaPuntuacions / numOpinions;
    }

    public static List<Local> getLocals(List<Local> locals, int id) throws ParseException, SQLException {
        Connection conexio = ConexioBD.CONN();
        String sql = "SELECT * FROM local WHERE id_usuari="+ id;
        List<Opinio> opinions = null;
        List<Tapa> tapes = null;

        locals = new ArrayList<>();

        Statement stmt;
        ResultSet rs;
        try {
            stmt = conexio.createStatement();
            rs = stmt.executeQuery(sql);
            Statement stmt2 = conexio.createStatement();
            Statement stmt3 = conexio.createStatement();
            Statement stmt4 = conexio.createStatement();
            Statement stmt5 = conexio.createStatement();

            while (rs.next()) {
                int id_local = rs.getInt("id");
                String nom_local = rs.getString("nom");
                String direccio_local = rs.getString("direccio");
                String telefon_local = rs.getString("telefon");
                int id_horari = rs.getInt("id_horari");
                String descripcio = rs.getString("descripcio");

                String horari = Utilitats.queryHorari(stmt2, id_horari);
                tapes = Utilitats.queryTapes(stmt3, tapes, id_local);
                opinions = Utilitats.queryOpinions(stmt4, opinions, id_local);
                String link_foto = Utilitats.queryFotoPrincipal(stmt5, id_local);

                Double mitjana= Utilitats.calcularMitjanaPuntuacio(opinions);

                locals.add(new Local(id_local,link_foto,nom_local,direccio_local,horari,mitjana,telefon_local,descripcio,tapes,opinions));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        conexio.close();
        return locals;
    }

    public static int agafarIdShared(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences("MyPrefs", MODE_PRIVATE);
        return sharedPref.getInt("id", 0);
    }

    public static void visibilitatConsumidors(MenuItem menuItemFavorits, MenuItem menuItemDescomptes, MenuItem menuItemLesMevesDades, MenuItem menuItemXat, MenuItem menuItemLocals, MenuItem menuItemTapes, MenuItem menuItemComentaris) {
        menuItemLocals.setVisible(false);
        menuItemTapes.setVisible(false);
        menuItemComentaris.setVisible(false);
        menuItemXat.setVisible(false);
        menuItemLesMevesDades.setVisible(true);
        menuItemDescomptes.setVisible(true);
        menuItemFavorits.setVisible(true);
    }

    public static void visibilitatLocals(MenuItem menuItemFavorits, MenuItem menuItemDescomptes, MenuItem menuItemLesMevesDades, MenuItem menuItemXat, MenuItem menuItemLocals, MenuItem menuItemTapes, MenuItem menuItemComentaris) {
        menuItemLocals.setVisible(true);
        menuItemTapes.setVisible(true);
        menuItemComentaris.setVisible(true);
        menuItemXat.setVisible(true);
        menuItemLesMevesDades.setVisible(false);
        menuItemDescomptes.setVisible(false);
        menuItemFavorits.setVisible(false);
    }

    public static int getTipusUsuari(int id) throws SQLException {
        Connection conexio = ConexioBD.CONN();
        String sql = "SELECT * FROM local WHERE id_usuari=" + id;
        Statement stmt = null;
        ResultSet rs = null;
        boolean esLocal = false;

        try {
            stmt = conexio.createStatement();
            rs = stmt.executeQuery(sql);

            if (rs.next()) {
                esLocal = true;

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        conexio.close();
        if (esLocal) {
            return 2;
        } else {
            return 1;
        }
    }


    @SuppressLint("NonConstantResourceId")
    public static Intent gestioDeMenu(MenuItem item, Intent intent, String nom, Context context, String correu) {
        // S'executa una acció segons el botó premut del menú Drawer
        switch (item.getItemId()) {
            case R.id.btn_dades:
                if (nom != null) {
                    intent = new Intent(context, LesMevesDades.class);
                    intent.putExtra("usuari", correu);
                } else {
                    intent = new Intent(context, IniciSessio.class);
                }
                break;
            case R.id.btn_preferits:
                if (nom != null) {
                    intent = new Intent(context, ElsMeusFavorits.class);
                    intent.putExtra("usuari", correu);
                } else {
                    intent = new Intent(context, IniciSessio.class);
                }
                break;
            case R.id.btn_descompte:
                if (nom != null) {
                    intent = new Intent(context, ElsMeusDescomptes.class);
                    intent.putExtra("usuari", correu);
                } else {
                    intent = new Intent(context, IniciSessio.class);
                }
                break;
            case R.id.btn_noticies:
                intent = new Intent(context, Noticies.class);
                break;
            case R.id.btn_preguntes:
                intent = new Intent(context, PreguntesFrequents.class);
                break;
            case R.id.btn_contacte:
                intent = new Intent(context, Contacta.class);
                break;
            case R.id.btn_locals:
                intent = new Intent(context, ElsMeusLocals.class);
                break;
            case R.id.btn_tapes:
                // Es passa la informació del local a mostrar a l'activitat LesMevesTapes
                intent = new Intent(context, LesMevesTapes.class);
                break;
            case R.id.btn_comentaris:
                intent = new Intent(context, Comentaris.class);
                break;
            case R.id.btn_xat:
                intent = new Intent(context, Chat.class);
                break;
            case R.id.btn_login:
                intent = new Intent(context, IniciSessio.class);
                break;
            case R.id.btn_close:

                SharedPreferences.Editor editor = context.getSharedPreferences("MyPrefs", MODE_PRIVATE).edit();
                editor.putBoolean("session_active", false);
                editor.remove("id");
                editor.remove("nom");
                editor.apply();
                intent = new Intent(context, Inici.class);
                break;
        }
        return intent;
    }

}
