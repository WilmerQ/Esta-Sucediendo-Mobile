package co.edu.ucc.estasucediendo;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class tips extends AppCompatActivity {

    Button llamarBomberos;
    Button llamarCruzRoja;
    Button llamarDefensaCivil;
    Button llamarPolicia;
    Button llamarcrue;
    TextView titulo;
    TextView cuerpo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title_tips);
        setSupportActionBar(toolbar);
        mTitle.setText("Recomendaciones");
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        llamarBomberos = (Button) findViewById(R.id.llamar_bomberos);
        llamarCruzRoja = (Button) findViewById(R.id.llamar_cruzroja);
        llamarDefensaCivil = (Button) findViewById(R.id.llamar_defensacivil);
        llamarPolicia = (Button) findViewById(R.id.llamar_policia);
        llamarcrue = (Button) findViewById(R.id.llamar_crue);
        titulo = (TextView) findViewById(R.id.textViewNombreTipoEvento);
        cuerpo = (TextView) findViewById(R.id.textViewCuerpoTips);

        String s = getIntent().getExtras().getString("tipo_evento");
        Log.d("ESTASUCEDIENDO", "--------------" + s);

        titulo.setText(s);
        if (s.equals("Inundación")) {
            cuerpo.setText("• Defina rutas y sitios seguros de evacuación en zonas altas, lejos de las zonas inundables.\n" +
                    "• Participe en los simulacros de evacuación. \n" +
                    "• Tenga a mano una maleta con documentos y medicamentos importantes, provisiones de agua y alimento para la familia. \n" +
                    "• Incluya a los animales dentro de sus planes. \n" +
                    "• El ganado y las aves de corral también cuentan. \n" +
                    "• Acate las indicaciones de evacuación de las autoridades.\n" +
                    "• Regrese a su vivienda cuando lo indiquen las autoridades. \n" +
                    "• Verifique el estado de su vivienda y las instalaciones eléctricas, de gas y agua, antes de volver a habitarla.\n");
        } else if (s.equals("Accidentes de transito")) {
            cuerpo.setText("•\tVerificar el estado de las personas involucradas en el accidente. \n" +
                    "• Después, enciende las luces de emergencia del auto\n" +
                    "• Coloca los conos o triángulos reflectivos que guardas en tu equipo de carretera a 30 metros de la ubicación del automóvil siniestrado, con el fin de que cualquier vehículo que transite por el lugar, a 50 kilómetros por hora, alcance a detenerse o esquivar a las personas y vehículos afectados.\n" +
                    "• Llama inmediatamente a las autoridades que pueden ayudarte. Llama primero a la Línea de Emergencias (123), si es del caso, a la Policía de Carreteras (#767) y a tu aseguradora \n" +
                    "• Si se presentan heridos graves, no los muevas, pues puedes generarles heridas o lesiones más graves de las que ocasionó el accidente de tránsito. La única excepción a esta importante regla es que en lugar del accidente pueda presentarse, de manera inminente, un incendio, una inundación o un deslizamiento de tierra. Si los heridos presentan hemorragias, toma un pedazo de gasa y presiónalo sobre la herida y espera que llegue el equipo paramédico. Si los heridos no presentan gravedad, diles que no permanezcan sobre la vía ni cerca del lugar del accidente.\n" +
                    "• No muevas tu vehículo del lugar del accidente. Es la única manera que tienen las autoridades de tránsito de saber lo que realmente ocurrió. Hazlo solo cuando las autoridades de tránsito te lo indiquen. \n" +
                    "• Anota (si los hay) los nombres y números telefónicos de las personas que presenciaron el accidente (testigos). \n");
        } else if (s.equals("Tsunamis")) {
            cuerpo.setText("• Participe en los simulacros de evacuación del municipio, identifique las rutas y zonas de evacuación \n" +
                    "• Identifique la alarma en caso de tsunami. \n" +
                    "• Si observa que el mar se aleja o disminuye su nivel aléjese de inmediato de la costa y diríjase a un sitio alto, evacúe de manera organizada, camine rápido, no corra. El mar regresará con olas de gran altura y fuerza. \n" +
                    "• Acuerde un sitio de encuentro y tenga a mano una maleta con documentos y medicamentos importantes, provisiones de agua y alimento para la familia. \n" +
                    "• Si vive cerca de la playa y ocurre un sismo evacúe de inmediato al sitio seguro, esa es su alarma personal, no espere indicaciones. \n" +
                    "• No se retire de las zonas seguras hasta que lo indiquen las autoridades del Sistema Nacional de Gestión del Riesgo de Desastre. \n" +
                    "• Inicie las labores de limpieza y remoción de escombros cuando las autoridades lo indiquen.\n");
        } else if (s.equals("Incendios forestales")) {
            cuerpo.setText("• Dé aviso a los organismos operativos sobre la aparición de columnas de humo. \n" +
                    "• Si está en una zona de riesgo frente al incendio en curso, evacúe y ubíquese en un lugar seguro. \n" +
                    "• Permanezca en las zonas seguras hasta cuando las autoridades lo indiquen. \n" +
                    "• Mantenga herramientas que puedan apoyar las labores de extinción de fuego, ayude a la construcción de corta fuegos. \n" +
                    "• Limite el acceso de animales a las áreas quemadas. \n" +
                    "• Siembra semillas de rápido crecimiento.\n");
        } else if (s.equals("Sismo")) {
            cuerpo.setText("• Participa en los simulacros ante sismos.\n" +
                    "• Identifica zonas seguras y punto de encuentro con tu familia.\n" +
                    "• Ten listo un kit de emergencia.\n" +
                    "• Durante un sismo ubícate en los sitios seguros. Agáchate, cúbrete y sujétate.\n" +
                    "• Antes de evacuar cierra registros de agua, energía y gas.\n" +
                    "• Permanece atento a posibles réplicas.   \n");
        } else if (s.equals("Tormentas eléctricas")) {
            cuerpo.setText("• Manténgase dentro de la vivienda y desconecte los aparatos eléctricos. \n" +
                    "• Evite la cercanía a estructuras metálicas. \n" +
                    "• Aléjese de ríos, lagos y piscinas. \n" +
                    "• Aléjese de los árboles grandes y aislados en el campo. \n" +
                    "• Si no hay ningún lugar de refugio, póngase en cuclillas, tápese los oídos, evite el menor contacto con el suelo, esto le ayudará a ser menos vulnerable.\n" +
                    "• Si está en campo abierto busque refugio inmediatamente. \n" +
                    "• Informe a las empresas de servicios sobre caídas de cables y postes. \n" +
                    "• Evite estar cerca de estructuras altas como torres, árboles altos, cercos, líneas telefónicas o tendidos eléctricos. \n" +
                    "• Si existen personas afectadas, busque ayuda de emergencia de inmediato. \n");
        } else if (s.equals("Vendavales")) {
            cuerpo.setText("• Identifique sitios seguros para refugio. \n" +
                    "• Observe partes o elementos de las edificaciones o el entorno que puedan caer y aléjate de ellas. \n" +
                    "• Aléjese de árboles, cables y postes de energía.\n" +
                    "• Revise y asegure las estructuras y elementos de su vivienda que pudieron quedar sueltos. \n" +
                    "• Retire escombros de los cauces de agua y alcantarillados. \n" +
                    "• Apoye las labores de recuperación del Sistema Nacional de Gestión del Riesgo.\n");
        } else if (s.equals("Huracanes")) {
            cuerpo.setText("• Averigüe cual es la alarma de tu municipio en caso de huracán). \n" +
                    "• Participe en los simulacros de evacuación e identifique las rutas y zonas de evacuación que definan las autoridades. \n" +
                    "• Acuerde con su familia un sitio de encuentro en una zona segura. \n" +
                    "• Mantenga preparado un kit de emergencia. \n" +
                    "• Aprovisione alimentos, agua y elementos importantes para la familia. \n" +
                    "• Protéjase de cables de energía y postes de luz que puedan caer. \n" +
                    "• Apoye las labores de recuperación del Sistema nacional para la Gestión del Riesgo. \n" +
                    "• Manténgase al tanto de la información suministrada por los medios de comunicación.\n");
        }

        llamarBomberos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = "119";
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + number));
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startActivity(intent);
            }
        });

        llamarCruzRoja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = "132";
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + number));
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startActivity(intent);
            }
        });

        llamarDefensaCivil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = "144";
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + number));
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startActivity(intent);
            }
        });

        llamarPolicia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = "123";
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + number));
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startActivity(intent);
            }
        });

        llamarcrue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = "0354209632";
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + number));
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startActivity(intent);
            }
        });

    }

}
