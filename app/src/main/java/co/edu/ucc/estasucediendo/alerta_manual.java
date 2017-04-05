package co.edu.ucc.estasucediendo;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import co.edu.ucc.estasucediendo.SqLite.parametroBD;
import co.edu.ucc.estasucediendo.clases.AlertaManual;
import co.edu.ucc.estasucediendo.clases.Conexion;
import co.edu.ucc.estasucediendo.ui.ViewProxy;
import okhttp3.Headers;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class alerta_manual extends AppCompatActivity implements OnMapReadyCallback {

    public final static int RESP_TOMAR_FOTO = 1000;
    public final static int RESP_TOMAR_Video = 1000;
    Boolean tomandoFoto = Boolean.FALSE;
    Boolean tomandoVideo = Boolean.FALSE;
    private final String ruta = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/esta_sucediendo/";
    File file = new File(ruta);
    File audio = new File(ruta);
    File clase;
    private static final int MY_PERMISSIONS_REQUEST_GLOBAL = 1;
    Spinner spinnerNivelesAlerta;
    public LocationManager mLocationManager;
    public LocationUpdaterListener mLocationListener;
    AlertaManual alertaManual;
    AlertDialog alert = null;
    MapFragment mapFragment;
    LatLng temp;
    double radio;
    ImageButton buttonFoto;
    Button enviarAlerta;
    //Handler mHandler = new Handler();
    Bitmap original;
    VideoView videoView;
    ProgressDialog dialog;
    //ProgressDialog progress;
    ImageView imagen;
    ImageButton grabarVideo;
    MediaRecorder recorder = new MediaRecorder();

    private float distCanMove = dp(80);
    private View slideText;
    private float startedDraggingX = -1;
    private View recordPanel;
    private long startTime = 0L;
    long timeInMilliseconds = 0L;
    private Timer timer;
    long updatedTime = 0L;
    long timeSwapBuff = 0L;
    private TextView recordTimeText;
    private ImageButton audioSendButton;
    ImageView rec;
    ImageView imagen_arrow;
    TextView textView;
    //llamando a bd
    parametroBD bd;
    private boolean bVideoIsBeingTouched = false;
    private Handler mHandler = new Handler();
    String imagenConvertida;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alerta_manual);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title1);
        setSupportActionBar(toolbar);
        mTitle.setText("Reporte Alerta");
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.fragmentVerMapa1);
        videoView = (VideoView) findViewById(R.id.videoView);
        buttonFoto = (ImageButton) findViewById(R.id.buttonFoto);
        enviarAlerta = (Button) findViewById(R.id.buttonEnviarAlerta);
        grabarVideo = (ImageButton) findViewById(R.id.buttonVideo);
        imagen = (ImageView) findViewById(R.id.imageView2);
        slideText = findViewById(R.id.slideText);
        recordPanel = findViewById(R.id.record_panel);
        recordTimeText = (TextView) findViewById(R.id.recording_time_text);
        textView = (TextView) findViewById(R.id.slideToCancelTextView);
        imagen_arrow = (ImageView) findViewById(R.id.imagen_arrow);
        audioSendButton = (ImageButton) findViewById(R.id.chat_audio_send_button);
        rec = (ImageView) findViewById(R.id.rec);

        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setTitle("Reportando.");
        dialog.setMessage("Subiendo Informacion.... Por favor espere.");
        dialog.setCancelable(false);
        dialog.setMax(100);

        videoView.setVisibility(View.INVISIBLE);
        imagen.setVisibility(View.INVISIBLE);
        alertaManual = new AlertaManual();
        bd = new parametroBD(alerta_manual.this);
        final Cursor cursor = bd.consultarUsuario();
        cursor.moveToFirst();
        enviarAlerta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertaManual.setNombrePersona(cursor.getString(1));
                alertaManual.setCorreo(cursor.getString(2));
                alertaManual.setCelular(cursor.getString(3));
                if (VerificarFormulario()) {
                    Log.d("ESTASUCEDIENDO", "--paso verificacion");
                    dialog.show();
                    dialog.setProgress(0);
                    new SubirDatos().execute();
                }
            }
        });

        List<String> permisos = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == -1)
            permisos.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == -1)
            permisos.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == -1)
            permisos.add(android.Manifest.permission.CAMERA);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) == -1)
            permisos.add(android.Manifest.permission.RECORD_AUDIO);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == -1)
            permisos.add(Manifest.permission.CALL_PHONE);

        Log.d("ESTASUCEDIENDO", "tamaño Permisos: " + permisos.size());
        if ((ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == -1) ||
                (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == -1) ||
                (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == -1) ||
                (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == -1) ||
                (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) == -1)) {
            String[] strings = new String[permisos.size()];
            for (int i = 0; i < permisos.size(); i++) {
                Log.d("ESTASUCEDIENDO", "Permisos Restastes: " + permisos.get(i));
                strings[i] = permisos.get(i);
            }
            Log.d("ESTASUCEDIENDO", "Permiso: primero " + strings.length);
            ActivityCompat.requestPermissions(alerta_manual.this, strings, MY_PERMISSIONS_REQUEST_GLOBAL);
        }
        Log.d("ESTASUCEDIENDO", "creando directorio ----- " + file.mkdir());
        Log.d("ESTASUCEDIENDO", "creando directorio ----- " + audio.mkdir());

        imagen_arrow.setVisibility(View.INVISIBLE);
        audioSendButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Log.d("ESTASUCEDIENDO", "-----------Action DOWM");
                    imagen_arrow.setVisibility(View.VISIBLE);
                    textView.setText("Cancelar");
                    mostrarMensaje("Presione para grabar, suelte para guardar", Toast.LENGTH_SHORT);
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) slideText.getLayoutParams();
                    params.leftMargin = dp(60);
                    slideText.setLayoutParams(params);
                    ViewProxy.setAlpha(slideText, 1);
                    startedDraggingX = -1;
                    rec.setImageResource(R.drawable.rec);
                    startRecording(true);
                    startrecord();
                    audioSendButton.getParent().requestDisallowInterceptTouchEvent(true);
                    recordPanel.setVisibility(View.VISIBLE);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    Log.d("ESTASUCEDIENDO", "-----------Action UP");
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) slideText.getLayoutParams();
                    float alpha = 0;
                    ViewProxy.setAlpha(slideText, alpha);
                    startedDraggingX = -1;
                    startRecording(false);
                    stoprecord();
                } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                    Log.d("ESTASUCEDIENDO", "-----------Action Cancel");
                    startedDraggingX = -1;
                    startRecording(false);
                    stoprecord();
                    recordTimeText.setText("00:00");
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    Log.d("ESTASUCEDIENDO", "-----------Action Move");
                    float x = event.getX();
                    if (x < -distCanMove) {
                        if (!recordTimeText.getText().equals("00:00")) {
                            Log.d("ESTASUCEDIENDO", "-----------Action Move-------");
                            stoprecord();
                            recordTimeText.setText("00:00");
                        }
                    }
                    x = x + ViewProxy.getX(audioSendButton);
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) slideText
                            .getLayoutParams();
                    if (startedDraggingX != -1) {
                        float dist = (x - startedDraggingX);
                        params.leftMargin = dp(60) + (int) dist;
                        slideText.setLayoutParams(params);
                        float alpha = 1.0f + dist / distCanMove;
                        if (alpha > 1) {
                            alpha = 1;
                        } else if (alpha < 0) {
                            alpha = 0;
                        }
                        ViewProxy.setAlpha(slideText, alpha);
                    }
                    if (x <= ViewProxy.getX(slideText) + slideText.getWidth()
                            + dp(30)) {
                        if (startedDraggingX == -1) {
                            startedDraggingX = x;
                            distCanMove = (recordPanel.getMeasuredWidth()
                                    - slideText.getMeasuredWidth() - dp(48)) / 2.0f;
                            if (distCanMove <= 0) {
                                distCanMove = dp(80);
                            } else if (distCanMove > dp(80)) {
                                distCanMove = dp(80);
                            }
                        }
                    }
                    if (params.leftMargin > dp(60)) {
                        params.leftMargin = dp(60);
                        slideText.setLayoutParams(params);
                        ViewProxy.setAlpha(slideText, 1);
                        startedDraggingX = -1;
                    }
                }
                v.onTouchEvent(event);
                return true;
            }
        });


        grabarVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tomandoVideo = Boolean.TRUE;
                buttonFoto.setVisibility(View.INVISIBLE);
                recordPanel.setVisibility(View.INVISIBLE);
                audioSendButton.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                String filePath = ruta + "/video.mp4";
                Log.d("ESTASUCEDIENDO", "filePath---------- " + filePath);
                Uri output = Uri.fromFile(new File(filePath));
                intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 120);
                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, "1");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, output);
                Log.d("ESTASUCEDIENDO", "Uri---------- " + output);
                startActivityForResult(intent, RESP_TOMAR_Video);
            }
        });

        buttonFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertaManual.setImagen(null);
                tomandoFoto = Boolean.TRUE;
                grabarVideo.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                String filePath = ruta + "/foto.jpg";
                Log.d("ESTASUCEDIENDO", "filePath---------- " + filePath);
                Uri output = Uri.fromFile(new File(filePath));
                intent.putExtra(MediaStore.EXTRA_OUTPUT, output);
                Log.d("ESTASUCEDIENDO", "Uri---------- " + output);
                startActivityForResult(intent, RESP_TOMAR_FOTO);
            }
        });

        spinnerNivelesAlerta = (Spinner) findViewById(R.id.spinner2);

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = new LocationUpdaterListener();
        if (mLocationManager.getProviders(true).contains(LocationManager.GPS_PROVIDER)) {
            Log.d("ESTASUCEDIENDO", "ubicacion activada");
            if (mLocationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER)) {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
            }
        } else {
            Log.d("ESTASUCEDIENDO", "gps desactivado");
            AlertNoGps();
        }
        List<String> ejemplo = new LinkedList<>();
        ejemplo.add("Seleccione");
        ejemplo.add("Inundación");
        ejemplo.add("Accidentes de transito");
        ejemplo.add("Tsunamis");
        ejemplo.add("Incendios forestales");
        ejemplo.add("Sismo");
        ejemplo.add("Tormentas eléctricas");
        ejemplo.add("Vendavales");
        ejemplo.add("Huracanes");

        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_dropdown_item, ejemplo);
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNivelesAlerta.setAdapter(adaptador);
        spinnerNivelesAlerta.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!parent.getSelectedItem().toString().equals("")) {
                    alertaManual.setTipoEvento(parent.getSelectedItem().toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_alerta_manual, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_politicaPrivacidad) {
            Uri uri = Uri.parse("http://www.ucc.edu.co/asuntos-legales/Paginas/tratamiento-de-datos-personales-.aspx");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public static int dp(float value) {
        return (int) Math.ceil(1 * value);
    }

    private void startRecording(Boolean rec) {
        if (rec) {
            Boolean deleted = Boolean.FALSE;
            File file1 = new File(ruta + "nota.mp3");
            if (file1.exists()) {
                File file = new File(ruta + "nota.mp3");
                deleted = file.delete();
                Log.d("ESTASUCEDIENDO", "eliminar archivo " + deleted);
            }
            try {
                if (deleted) {
                    recorder = new MediaRecorder();
                }
                recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
                recorder.setOutputFile(ruta + "nota.mp3");
                recorder.prepare();
                recorder.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            recorder.stop();
            recorder.reset();
            recorder.release();
            recorder = new MediaRecorder();

            File temp = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/esta_sucediendo/nota.mp3");
            byte[] bytes = new byte[(int) temp.length()];
            try {
                BufferedInputStream buf = new BufferedInputStream(new FileInputStream(temp));
                buf.read(bytes, 0, bytes.length);
                buf.close();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            alertaManual.setAudio(bytes);
        }
    }

    private void AlertNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.URTransparent));
        builder.setMessage("El sistema GPS esta desactivado, ¿Desea activarlo?")
                .setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                        //getApplicationContext().stopService(new Intent(getApplicationContext(), MyService.class));
                        finishAndRemoveTask();
                    }
                });
        alert = builder.create();
        alert.show();
    }

    public boolean isSdReadable() {
        boolean mExternalStorageAvailable = false;
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            mExternalStorageAvailable = true;
            Log.i("isSdReadable", "External storage card is readable.");
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // We can only read the media
            Log.i("isSdReadable", "External storage card is readable.");
            mExternalStorageAvailable = true;
        } else {
            // Something else is wrong. It may be one of many other
            // states, but all we need to know is we can neither read nor write
            mExternalStorageAvailable = false;
        }
        return mExternalStorageAvailable;
    }

    public Bitmap getThumbnail(String filename) {
        String fullPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/esta_sucediendo/";
        Bitmap thumbnail = null;
        // Look for the file on the external storage
        try {
            if (isSdReadable()) {
                thumbnail = BitmapFactory.decodeFile(fullPath + "/" + filename);
            }
        } catch (Exception e) {
            Log.e("ESTASUCEDIENDO", "getThumbnail() on external storage" + e.getMessage());
        }
        // If no file on external storage, look in internal storage
        if (thumbnail == null) {
            try {
                File filePath = getApplicationContext().getFileStreamPath(filename);
                FileInputStream fi = new FileInputStream(filePath);
                thumbnail = BitmapFactory.decodeStream(fi);
            } catch (Exception ex) {
                Log.d("ESTASUCEDIENDO", "NO SE ENCONTRO " + ex.getMessage());
            }
        }
        //original = thumbnail;
        if ((thumbnail.getHeight() > 4096) || (thumbnail.getWidth() > 4096)) {
            Log.d("ESTASUCEDIENDO", "Tamaño muy grande");
            Bitmap temp = Bitmap.createScaledBitmap(thumbnail, (int) thumbnail.getWidth() / 2, (int) thumbnail.getHeight() / 2, true);
            thumbnail = temp;
        }
        //original = thumbnail;
        return thumbnail;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == RESP_TOMAR_FOTO && resultCode == RESULT_OK) && (tomandoFoto)) {
            Log.d("ESTASUCEDIENDO", "--------tomo la fot y volvio");
            original = orientantoImagen(getThumbnail("foto.jpg"));
            imagen.setImageBitmap(original);
            imagen.setVisibility(View.VISIBLE);
            new ConvertirImagen().execute();
        }
        if ((requestCode == RESP_TOMAR_Video && resultCode == RESULT_OK) && (tomandoVideo)) {
            Log.d("ESTASUCEDIENDO", "--------tomo el video y volvio");
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/esta_sucediendo/video.mp4");
            int size = (int) file.length();
            byte[] bytes = new byte[size];
            try {
                BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
                buf.read(bytes, 0, bytes.length);
                buf.close();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            alertaManual.setVideo(bytes);
            videoView.setVideoPath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/esta_sucediendo/video.mp4");
            videoView.setVisibility(View.VISIBLE);
            videoView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        if (!bVideoIsBeingTouched) {
                            bVideoIsBeingTouched = true;
                            if (videoView.isPlaying()) {
                                videoView.pause();
                            } else {
                                videoView.resume();
                            }
                            if (!videoView.isPlaying()) {
                                videoView.start();
                            }
                        }
                        bVideoIsBeingTouched = false;
                    }
                    return true;
                }
            });
            videoView.start();
        }

    }

    public Bitmap orientantoImagen(Bitmap foto) {
        Bitmap rotatedBitmap;
        try {
            ExifInterface anInterface = new ExifInterface(ruta + "foto.jpg");
            int orientation = anInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            Log.d("ESTASUCEDIENDO", "orientantoImagen: " + orientation);
            if (orientation == 1) {
                return foto;
            } else if (orientation == 6) {
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                rotatedBitmap = Bitmap.createBitmap(foto, 0, 0, foto.getWidth(), foto.getHeight(), matrix, true);
                return rotatedBitmap;
            } else if (orientation == 3) {
                Matrix matrix = new Matrix();
                matrix.postRotate(180);
                rotatedBitmap = Bitmap.createBitmap(foto, 0, 0, foto.getWidth(), foto.getHeight(), matrix, true);
                return rotatedBitmap;
            } else if (orientation == 8) {
                Matrix matrix = new Matrix();
                matrix.postRotate(270);
                rotatedBitmap = Bitmap.createBitmap(foto, 0, 0, foto.getWidth(), foto.getHeight(), matrix, true);
                return rotatedBitmap;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return foto;
    }

    public class LocationUpdaterListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            radio = location.getAccuracy();
            temp = new LatLng(location.getLatitude(), location.getLongitude());
            mapFragment.getMapAsync(alerta_manual.this);
            //mapFragment.onStart();
            Log.d("ESTASUCEDIENDO", "onLocationChanged " + location.getLatitude() + " : " + location.getLongitude() + " : " + location.getAccuracy());
            if (location.getAccuracy() < 15) {
                Log.d("ESTASUCEDIENDO", "Ubicacion con 10 metros de precision");
                alertaManual.setLatitud(location.getLatitude());
                alertaManual.setLongitud(location.getLongitude());
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mLocationManager.removeUpdates(mLocationListener);
                }
                mLocationManager.removeUpdates(mLocationListener);
                mapFragment.getMapAsync(alerta_manual.this);
                mapFragment.onStart();
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        @Override
        public void onProviderEnabled(String provider) {
        }

    }

    @Override
    protected void onRestart() {
        Log.d("ESTASUCEDIENDO", "onRestart");
        if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
        }
        super.onRestart();
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

    /**
     * clase Descagar
     * <br>
     * clase encarga de implementar el AsyncTask interfaz optimizada para comunicacion de datos desde servidor a el dispositivo.
     *
     * @author Wilmer
     * @see AsyncTask
     */
    public class SubirDatos extends AsyncTask<String, String, Boolean> {
        OkHttpClient client = new OkHttpClient();
        Response response = null;
        /**
         * The Gson.
         */
        Gson gson = new Gson();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                try {
                    if (!(alertaManual.getImagen().length() > 0)) {
                        publishProgress("Error: espere");
                        return false;
                    }
                }catch (Exception e){
                    publishProgress("Error: intentelo nuevamente");
                    return false;
                }


                String url = "http://" + Conexion.getLocalhost() + ":" + Conexion.getPuerto() + "/coe/webresources/auxiliar/";
                Log.d("ESTASUCEDIENDO", "URL: " + url);

                try {
                    if (alertaManual.getVideo() != null)
                        alertaManual.setAudio(null);
                } catch (Exception e) {
                }

                clase = new File(ruta, "clase.dat");
                ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(clase));
                os.writeObject(gson.toJson(alertaManual));
                os.flush();
                os.close();
                Log.d("ESTASUCEDIENDO", "clase:size " + clase.length());

                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addPart(Headers.of("Content-Disposition", "form-data; name=\"file\"; filename=\"file\""),
                                new CountingFileRequestBody(clase, "file/*", new CountingFileRequestBody.ProgressListener() {
                                    @Override
                                    public void transferred(long num) {
                                        float progress = (num / (float) clase.length()) * 100;
                                        publishProgress("" + (int) progress);
                                    }
                                }))
                        .build();

                Request request = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .addHeader("cache-control", "no-cache")
                        .build();

                String res;
                response = client.newCall(request).execute();

                if (response.code() == 200) {
                    res = response.body().string();
                    publishProgress("Respuesta " + res);
                    Log.d("ESTASUCEDIENDO", "elminar archivo clase: " + clase.delete());
                    return true;
                } else {
                    res = response.body().string();
                    Log.e("CoeMovil", "Error: " + res);
                    publishProgress("Error: " + res);
                    Log.d("ESTASUCEDIENDO", "elminar archivo clase: " + clase.delete());
                    return false;
                }
            } catch (IOException e) {
                if (clase.exists()) {
                    Log.d("ESTASUCEDIENDO", "elminar archivo clase: " + clase.delete());
                }
                e.printStackTrace();
                publishProgress("Error: " + e.getMessage());
                return null;
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            Log.d("ESTASUCEDIENDO", "publicprogress" + values[0]);
            if (!(values[0] == null)) {
                if ((values[0].contains("Respuesta")) || (values[0].contains("Error"))) {
                    mostrarMensaje(" " + values[0], Toast.LENGTH_LONG);
                } else {
                    dialog.setProgress(Integer.parseInt(values[0]));
                }
            }
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Boolean aVoid) {
            try {
                if (aVoid == null) {
                    mLocationManager.removeUpdates(mLocationListener);
                    dialog.dismiss();
                } else if (!aVoid) {
                    mLocationManager.removeUpdates(mLocationListener);
                    dialog.cancel();
                } else {
                    dialog.cancel();
                    mLocationManager.removeUpdates(mLocationListener);
                    Intent i = new Intent(alerta_manual.this, tips.class);
                    i.putExtra("tipo_evento", alertaManual.getTipoEvento());
                    mostrarMensaje("Se ha Reportado", Toast.LENGTH_SHORT);
                    startActivity(i);
                    finish();
                }
            } catch (JsonSyntaxException e) {
                dialog.cancel();
                e.printStackTrace();
            }
            super.onPostExecute(aVoid);
        }
    }

    /**
     * clase Descagar
     * <br>
     * clase encarga de implementar el AsyncTask interfaz optimizada para comunicacion de datos desde servidor a el dispositivo.
     *
     * @author Wilmer
     * @see AsyncTask
     */
    public class ConvertirImagen extends AsyncTask<String, String, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                if (!(original == null)) {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    original.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] bytes = new byte[stream.size()];
                    bytes = stream.toByteArray();
                    String s = Base64.encodeToString(bytes, Base64.DEFAULT);
                    Log.d("ESTASUCEDIENDO", "s: " + bytes.length);
                    alertaManual.setImagen(s);
                    return true;
                } else {
                    publishProgress("Error: ");
                    return false;
                }
            } catch (Exception e) {
                publishProgress("Error: " + e.getMessage());
                return null;
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            Log.d("ESTASUCEDIENDO", "publicprogress" + values[0]);
            if (!(values[0] == null)) {
                if ((values[0].contains("Respuesta")) || (values[0].contains("Error"))) {
                    mostrarMensaje(" " + values[0], Toast.LENGTH_LONG);
                }
            }
            super.onProgressUpdate(values);
        }
    }

    class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMilliseconds;
            final String hms = String.format(
                    "%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(updatedTime)
                            - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
                            .toHours(updatedTime)),
                    TimeUnit.MILLISECONDS.toSeconds(updatedTime)
                            - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
                            .toMinutes(updatedTime)));
            long lastsec = TimeUnit.MILLISECONDS.toSeconds(updatedTime)
                    - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
                    .toMinutes(updatedTime));
            System.out.println(lastsec + " hms " + hms);
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    try {
                        if (recordTimeText != null)
                            recordTimeText.setText(hms);
                    } catch (Exception e) {
                        // TODO: handle exception
                    }

                }
            });
        }

    }

    private void startrecord() {
        // TODO Auto-generated method stub
        startTime = SystemClock.uptimeMillis();
        timer = new Timer();
        MyTimerTask myTimerTask = new MyTimerTask();
        timer.schedule(myTimerTask, 1000, 1000);
        vibrate();
    }

    private void stoprecord() {
        // TODO Auto-generated method stub
        imagen_arrow.setVisibility(View.INVISIBLE);
        rec.setImageResource(R.drawable.ic_brightness_1_black_24dp);

        if (timer != null) {
            timer.cancel();
        }
        if (recordTimeText.getText().toString().equals("00:00")) {
            return;
        }
        vibrate();
    }

    private void vibrate() {
        // TODO Auto-generated method stub
        try {
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(200);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.clear();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.getUiSettings().setRotateGesturesEnabled(false);
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(temp);
        circleOptions.radius(radio);
        circleOptions.fillColor(0x5500ff00);
        circleOptions.strokeWidth(1);
        googleMap.addCircle(circleOptions);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(temp, 16));

        if (radio < 15) {
            googleMap.clear();
            MarkerOptions options = new MarkerOptions();
            options.position(temp);
            options.draggable(false);
            options.title("Tu ubicación");
            googleMap.addMarker(options);
        }
    }

    public boolean VerificarFormulario() {
        boolean b = true;
        if (alertaManual.getTipoEvento().equals("Seleccione")) {
            mostrarMensaje("Selecione el origen del evento", Toast.LENGTH_SHORT);
            b = false;
        }

        if ((original == null) && (!tomandoVideo)) {
            mostrarMensaje("Tome una fotografia o Grabe un video", Toast.LENGTH_SHORT);
            b = false;
        }

        if ((alertaManual.getLatitud() == null) || (alertaManual.getLongitud() == null)) {
            mostrarMensaje("Espere, se esta determinando su ubicación", Toast.LENGTH_SHORT);
            b = false;
        }
        return b;
    }

}
