package co.edu.ucc.estasucediendo;

import android.*;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.List;

import co.edu.ucc.estasucediendo.SqLite.parametroBD;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final int MY_PERMISSIONS_REQUEST_GLOBAL = 1;
    GoogleApiClient mGoogleApiClient;
    GoogleSignInOptions gso;
    SignInButton signInButton;
    private static final int RC_SIGN_IN = 0;
    EditText nombre;
    EditText correoElectronico;
    EditText celular;
    Button guardar;
    //llamando a bd
    parametroBD bd;
    Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        mTitle.setText("Está Sucediendo");
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        bd = new parametroBD(MainActivity.this);

        Cursor cursor = bd.consultarUsuario();
        Log.d("ESTASUCEDIENDO", "cursor: " + cursor.getCount());
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            mostrarMensaje("Ingreso", Toast.LENGTH_SHORT);
            Intent i = new Intent(MainActivity.this, alerta_manual.class);
            startActivity(i);
            finish();
        }

        nombre = (EditText) findViewById(R.id.nombreUsuario);
        correoElectronico = (EditText) findViewById(R.id.correoelectronico);
        celular = (EditText) findViewById(R.id.editTextCelular);
        guardar = (Button) findViewById(R.id.buttonGuardar);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Set the dimensions of the sign-in button.
        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (VerificarFormulario()) {
                    bd.insertar_usuario(nombre.getText().toString(), correoElectronico.getText().toString(), celular.getText().toString());
                    Intent i = new Intent(MainActivity.this, alerta_manual.class);
                    mostrarMensaje("Se ha guardado", Toast.LENGTH_SHORT);
                    startActivity(i);
                    finish();
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_politicaPrivacidad_Main) {
            Uri uri = Uri.parse("http://www.ucc.edu.co/asuntos-legales/Paginas/tratamiento-de-datos-personales-.aspx");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("ESTASUCEDIENDO", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            Log.d("ESTASUCEDIENDO", "acct:" + acct.getDisplayName());
            nombre.setText(acct.getDisplayName());
            correoElectronico.setText(acct.getEmail());
            signInButton.setVisibility(View.INVISIBLE);
            //mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
            //updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
            // updateUI(false);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /**
     * metodo utilizado para mostrar un mensaje en pantalla
     * utilizando un elemento Toast
     *
     * @param mensaje
     * @param duracion
     */
    private void mostrarMensaje(final String mensaje, final int duracion) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), mensaje, duracion).show();
            }
        });
    }

    public boolean VerificarFormulario() {
        boolean b = true;
        if (nombre.getText().length() < 1) {
            mostrarMensaje("Ingrese Su Nombre", Toast.LENGTH_SHORT);
            b = false;
        }

        if (correoElectronico.getText().length() < 1) {
            mostrarMensaje("Ingrese correo electronico", Toast.LENGTH_SHORT);
            b = false;
        }

        if (celular.getText().length() < 1) {
            mostrarMensaje("Ingrese celular", Toast.LENGTH_SHORT);
            b = false;
        }
        return b;
    }

}
