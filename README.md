# 📱 JobBoard - Application de Recherche d'Emploi

Projet de fin de module : Développement Mobile.
Cette application permet aux utilisateurs de consulter des offres d'emploi, de gérer leurs candidatures, et d'interagir avec les recruteurs via les fonctionnalités natives du téléphone.

---

## ✅ Checklist du Cahier des Charges (Mapping des fonctionnalités)

Voici la correspondance exacte entre les exigences du projet et leur implémentation dans le code source :

---

### Fonctionnalités Métier

- [x] **Catalogue d'offres :** Implémenté via `OffresFragment` qui récupère une liste depuis une API.
  - 📄 `app/src/main/java/com/example/jobboard/fragments/OffresFragment.java`
    - Ligne **34** : déclaration de la classe `public class OffresFragment extends Fragment`
    - Lignes **65–110** : méthode `fetchJobOffers()` — appel Retrofit asynchrone vers l'API
    - Lignes **111–124** : méthode `loadFromAssets()` — fallback JSON local (`assets/job_offers.json`)
  - 📄 `app/src/main/assets/job_offers.json` — 5 offres réalistes (id, title, company, location, description)
  - 📄 `app/src/main/res/layout/fragment_offres.xml` — RecyclerView + ProgressBar + message d'erreur

- [x] **Localisation des entreprises sur carte :** Implémenté dans `DetailActivity` à l'aide de Google Maps.
  - 📄 `app/src/main/java/com/example/jobboard/DetailActivity.java`
    - Ligne **43** : `implements OnMapReadyCallback`
    - Lignes **71–78** : tableau `DEMO_COORDS` — 5 coordonnées GPS (Paris, Lyon, Marseille, Toulouse, Nantes)
    - Lignes **155–163** : initialisation du `SupportMapFragment` et appel `getMapAsync(this)`
    - Lignes **166–180** : méthode `onMapReady()` — placement du marqueur et zoom caméra
  - 📄 `app/src/main/res/layout/activity_detail.xml`
    - Lignes **72–89** : `<fragment android:name="com.google.android.gms.maps.SupportMapFragment" />`
  - 📄 `app/src/main/AndroidManifest.xml`
    - Ligne **30** : `<meta-data android:name="com.google.android.geo.API_KEY" />`

- [x] **Photo du CV / Documents joints :** Bouton "Photographier CV" dans `DetailActivity` appelant la caméra système.
  - 📄 `app/src/main/java/com/example/jobboard/DetailActivity.java`
    - Ligne **61** : variable `cameraLauncher` (`ActivityResultLauncher<Intent>`)
    - Lignes **141–149** : enregistrement du `ActivityResultLauncher` pour le résultat caméra
    - Lignes **228–235** : méthode `checkCameraPermissionAndLaunch()` — demande la permission `CAMERA`
    - Lignes **237–259** : méthode `launchCamera()` — Intent `MediaStore.ACTION_IMAGE_CAPTURE` + `FileProvider` (ligne **240**)
    - Lignes **261–271** : méthode `createImageFile()` — création du fichier `CV_TIMESTAMP.jpg`
  - 📄 `app/src/main/AndroidManifest.xml`
    - Ligne **9** : `<uses-permission android:name="android.permission.CAMERA" />`
    - Lignes **48–54** : déclaration du `FileProvider` (`androidx.core.content.FileProvider`)
  - 📄 `app/src/main/res/xml/file_paths.xml` — chemins autorisés pour le FileProvider
  - 📄 `app/src/main/res/layout/activity_detail.xml`
    - Lignes **107–115** : `Button id="photo_cv_button"` violet (fond `#7B1FA2`)
    - Lignes **117–124** : `ImageView id="cv_image_view"` — affiche la photo prise (visibility="gone" par défaut)

- [x] **Appel direct du recruteur :** Boutons "Appeler" et "SMS" dans `DetailActivity`.
  - 📄 `app/src/main/java/com/example/jobboard/DetailActivity.java`
    - Lignes **294–303** : méthode `makePhoneCall()` — Intent **`ACTION_DIAL`** avec `Uri.parse("tel:…")` (ligne **298**)
    - Lignes **305–314** : méthode `sendSMS()` — Intent **`ACTION_SENDTO`** avec `Uri.parse("smsto:…")` (ligne **309**)
  - 📄 `app/src/main/AndroidManifest.xml`
    - Ligne **6** : `<uses-permission android:name="android.permission.CALL_PHONE" />`
    - Lignes **13–15** : `<uses-feature android:name="android.hardware.telephony" android:required="false" />`
  - 📄 `app/src/main/res/layout/activity_detail.xml`
    - Lignes **128–143** : `Button id="call_button"` (vert) et `Button id="sms_button"` (bleu)

- [x] **Suivi des candidatures envoyées :** Implémenté via l'onglet `CandidaturesFragment`, avec persistance en base de données. Option de retrait incluse.
  - 📄 `app/src/main/java/com/example/jobboard/fragments/CandidaturesFragment.java`
    - Ligne **22** : déclaration de la classe
    - Lignes **44–55** : `onViewCreated()` — initialisation RecyclerView + `ApplicationAdapter` avec listener de suppression
    - Lignes **59–64** : `loadApplications()` — lecture depuis SQLite via `dbHelper.getAllApplications()`
    - Lignes **69–81** : `deleteApplication()` — appel `dbHelper.deleteApplication(id)` + `adapter.removeItem(position)`
    - Lignes **84–94** : `refreshUI()` — mise à jour du compteur et affichage de la vue vide
  - 📄 `app/src/main/java/com/example/jobboard/adapters/ApplicationAdapter.java`
    - Ligne **17** : `public class ApplicationAdapter extends RecyclerView.Adapter`
    - Lignes **22–27** : interface `OnDeleteClickListener`
    - Lignes **50–57** : méthode `removeItem(position)` — `notifyItemRemoved` + `notifyItemRangeChanged`
    - Lignes **106–112** : listener sur `delete_button` dans `bind()`
  - 📄 `app/src/main/res/layout/item_application.xml`
    - Lignes **51–59** : `ImageButton id="delete_button"` — icône corbeille rouge (`app:tint="#E53935"`)
  - 📄 `app/src/main/res/layout/fragment_candidatures.xml` — RecyclerView + vue vide (📭)

- [x] **Notifications de nouvelles offres / confirmation :** Notification locale déclenchée lors de l'appui sur le bouton "Postuler".
  - 📄 `app/src/main/java/com/example/jobboard/utils/NotificationHelper.java`
    - Ligne **15** : déclaration de la classe `NotificationHelper`
    - Lignes **27–43** : méthode `createNotificationChannel()` — crée le canal `jobboard_candidatures` (Android 8+)
    - Lignes **49–70** : méthode `sendApplicationNotification()` — `NotificationCompat.Builder` avec `BigTextStyle` et `PendingIntent`
  - 📄 `app/src/main/java/com/example/jobboard/MainActivity.java`
    - Ligne **45** : `NotificationHelper.createNotificationChannel(this)` — création du canal au démarrage
  - 📄 `app/src/main/java/com/example/jobboard/DetailActivity.java`
    - Lignes **213–225** : méthode `sendCandidatureNotification()` — demande permission `POST_NOTIFICATIONS` (Android 13+) puis appelle `NotificationHelper`
    - Ligne **197** : appel de `sendCandidatureNotification()` depuis `applyToJob()` après insertion en BDD
  - 📄 `app/src/main/AndroidManifest.xml`
    - Ligne **10** : `<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />`

- [x] **Préférences de recherche persistantes :** Gestion de mots-clés multiples dans `PreferencesFragment`.
  - 📄 `app/src/main/java/com/example/jobboard/fragments/PreferencesFragment.java`
    - Ligne **28** : déclaration de la classe
    - Ligne **39** : `private SharedPreferences sharedPreferences`
    - Ligne **41** : `private static final String KEYWORDS_SET_KEY = "search_keywords_set"` — clé de stockage du `Set`
    - Ligne **43** : `private Set<String> keywordsSet = new TreeSet<>()` — stockage local trié
    - Lignes **95–114** : méthode `addKeyword()` — validation, détection des doublons, ajout au Set, sauvegarde
    - Lignes **117–142** : méthode `addChip()` — création dynamique d'un `Chip` Material avec icône ✕
    - Lignes **144–151** : méthode `loadKeywords()` — `sharedPreferences.getStringSet()` (ligne **145**)
    - Lignes **153–159** : méthode `saveKeywords()` — `sharedPreferences.edit().putStringSet()` (ligne **157**)
    - Lignes **161–168** : méthode `clearAllKeywords()`
  - 📄 `app/src/main/res/layout/fragment_preferences.xml`
    - Lignes **46–70** : `TextInputLayout` + `TextInputEditText` Material + `Button id="add_keyword_button"`
    - Lignes **73–84** : `ChipGroup id="chip_group_keywords"`
    - Lignes **86–93** : `Button id="clear_all_button"` (OutlinedButton)

---

### Composants Techniques & Android

- [x] **ListView / RecyclerView :** Utilisation de `RecyclerView` (plus performant) et de ses Adapters personnalisés pour les listes d'offres et de candidatures.
  - 📄 `app/src/main/java/com/example/jobboard/adapters/JobAdapter.java`
    - Ligne **20** : `public class JobAdapter extends RecyclerView.Adapter<JobAdapter.JobViewHolder>`
    - Lignes **62–101** : `JobViewHolder` — `onCreateViewHolder`, `onBindViewHolder`, `bind()`
  - 📄 `app/src/main/java/com/example/jobboard/adapters/ApplicationAdapter.java`
    - Ligne **17** : `public class ApplicationAdapter extends RecyclerView.Adapter<ApplicationAdapter.ApplicationViewHolder>`
    - Lignes **72–113** : `ApplicationViewHolder` — affichage des données BDD réelles
  - 📄 `app/src/main/res/layout/fragment_offres.xml` — `RecyclerView id="recycler_view_offers"`
  - 📄 `app/src/main/res/layout/fragment_candidatures.xml` — `RecyclerView id="recycler_view_candidatures"`
  - 📄 `app/src/main/res/layout/item_job_offer.xml` — layout d'un item d'offre (CardView)
  - 📄 `app/src/main/res/layout/item_application.xml` — layout d'un item de candidature (CardView)

- [x] **Widgets :** Utilisation intensive de composants UI (`TextView`, `Button`, `EditText`, `BottomNavigationView`, `ChipGroup` pour les filtres).
  - 📄 `app/src/main/res/layout/activity_main.xml`
    - Lignes **17–60** : header stylisé `LinearLayout id="app_header"` + `BottomNavigationView id="bottom_navigation"`
  - 📄 `app/src/main/res/layout/fragment_preferences.xml`
    - `TextInputLayout`, `TextInputEditText`, `Button`, `ChipGroup`
  - 📄 `app/src/main/res/layout/activity_detail.xml`
    - Boutons : `apply_button`, `photo_cv_button`, `call_button`, `sms_button`, `back_button`
    - `ImageView id="cv_image_view"` — affichage photo CV
  - 📄 `app/src/main/res/menu/bottom_nav_menu.xml` — 3 items de navigation (Offres, Candidatures, Préférences)

- [x] **SharedPreferences :** Sauvegarde des mots-clés de recherche dans l'onglet Préférences.
  - 📄 `app/src/main/java/com/example/jobboard/fragments/PreferencesFragment.java`
    - Ligne **40** : `private static final String PREFS_NAME = "JobBoardPreferences"`
    - Ligne **41** : `private static final String KEYWORDS_SET_KEY = "search_keywords_set"`
    - Ligne **65** : `requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)`
    - Ligne **145** : `sharedPreferences.getStringSet(KEYWORDS_SET_KEY, new HashSet<>())` — lecture
    - Ligne **157** : `sharedPreferences.edit().putStringSet(KEYWORDS_SET_KEY, ...)` — écriture

- [x] **SQLite :** Classe `DatabaseHelper` (héritant de `SQLiteOpenHelper`) pour stocker, lire et supprimer l'historique des candidatures.
  - 📄 `app/src/main/java/com/example/jobboard/database/DatabaseHelper.java`
    - Ligne **17** : `public class DatabaseHelper extends SQLiteOpenHelper`
    - Lignes **22–41** : constantes de table et requête `CREATE TABLE applications (...)`
    - Lignes **57–66** : `onCreate()` / `onUpgrade()`
    - Lignes **74–92** : méthode `insertApplication()` — `db.insert()` avec vérification doublon
    - Lignes **99–127** : méthode `getAllApplications()` — `db.query()` + mapping `Cursor` → `List<Application>`
    - Lignes **134–144** : méthode `deleteApplication(int id)` — `db.delete()` par ID *(nouvelle méthode)*
    - Lignes **148–162** : méthode `isAlreadyApplied(int jobId)`
  - 📄 `app/src/main/java/com/example/jobboard/models/Application.java` — modèle avec `id, jobId, jobTitle, company, location, dateApplied, status`

- [x] **Fragments :** Architecture basée sur `BottomNavigationView` avec 3 fragments (`OffresFragment`, `CandidaturesFragment`, `PreferencesFragment`).
  - 📄 `app/src/main/java/com/example/jobboard/MainActivity.java`
    - Ligne **18** : `public class MainActivity extends AppCompatActivity`
    - Lignes **53–70** : `setOnItemSelectedListener` — navigation entre les 3 fragments
    - Lignes **74–77** : méthode `loadFragment()` — `getSupportFragmentManager().beginTransaction().replace()`
  - 📄 `app/src/main/java/com/example/jobboard/fragments/OffresFragment.java` — Ligne **34**
  - 📄 `app/src/main/java/com/example/jobboard/fragments/CandidaturesFragment.java` — Ligne **22**
  - 📄 `app/src/main/java/com/example/jobboard/fragments/PreferencesFragment.java` — Ligne **28**
  - 📄 `app/src/main/res/layout/activity_main.xml` — `FrameLayout id="fragment_container"`

- [x] **Intents Explicites :** Navigation de `MainActivity` vers `DetailActivity` (avec passage de données via `putExtra`).
  - 📄 `app/src/main/java/com/example/jobboard/adapters/JobAdapter.java`
    - Lignes **88–100** : méthode `openDetail()` — `new Intent(context, DetailActivity.class)` + `putExtra()` (job_id, job_title, job_company, job_location, job_description)
  - 📄 `app/src/main/java/com/example/jobboard/DetailActivity.java`
    - Lignes **103–107** : `intent.getIntExtra("job_id")` + `getStringExtra(...)` — réception des données
  - 📄 `app/src/main/AndroidManifest.xml`
    - Lignes **39–41** : `<activity android:name=".DetailActivity" android:exported="false" android:parentActivityName=".MainActivity" />`

- [x] **Intents Implicites :** Utilisation de `ACTION_DIAL` (Appel), `ACTION_SENDTO` (SMS), et `ACTION_IMAGE_CAPTURE` (Caméra).
  - 📄 `app/src/main/java/com/example/jobboard/DetailActivity.java`
    - Ligne **298** : `new Intent(Intent.ACTION_DIAL)` — appel téléphonique
    - Ligne **309** : `new Intent(Intent.ACTION_SENDTO)` — SMS
    - Ligne **240** : `new Intent(MediaStore.ACTION_IMAGE_CAPTURE)` — caméra

- [x] **Capacités système :** Accès à la Caméra, Composeur d'Appels, et application SMS par défaut.
  - 📄 `app/src/main/AndroidManifest.xml`
    - Ligne **6** : `android.permission.CALL_PHONE`
    - Ligne **9** : `android.permission.CAMERA`
    - Ligne **10** : `android.permission.POST_NOTIFICATIONS`
    - Lignes **13–16** : `<uses-feature>` pour téléphonie et caméra (`required="false"`)
  - 📄 `app/src/main/java/com/example/jobboard/DetailActivity.java`
    - Lignes **273–289** : `onRequestPermissionsResult()` — gestion des permissions runtime (CAMERA + POST_NOTIFICATIONS)

- [x] **Google Maps :** Intégration de `SupportMapFragment` et `OnMapReadyCallback` pour afficher le marqueur de l'entreprise.
  - 📄 `app/src/main/java/com/example/jobboard/DetailActivity.java`
    - Ligne **43** : `implements OnMapReadyCallback`
    - Lignes **155–163** : init `SupportMapFragment` + `getMapAsync(this)`
    - Ligne **166** : `public void onMapReady(@NonNull GoogleMap googleMap)`
    - Lignes **167–180** : création du `MarkerOptions`, `moveCamera`, `setZoomControlsEnabled`
  - 📄 `app/src/main/res/layout/activity_detail.xml`
    - Lignes **72–89** : `<fragment android:id="@+id/map_fragment" android:name="com.google.android.gms.maps.SupportMapFragment" />`
  - 📄 `app/src/main/AndroidManifest.xml` — Ligne **30** : clé API `com.google.android.geo.API_KEY`
  - 📄 `gradle/libs.versions.toml` — `playServicesMaps = "19.2.0"`

- [x] **Retrofit :** Interface `JobApiService` pour récupérer le JSON des offres (backend bouchonné avec données réalistes). Utilisation de `GsonConverterFactory`.
  - 📄 `app/src/main/java/com/example/jobboard/api/JobApiService.java`
    - Ligne **10** : `public interface JobApiService`
    - Ligne **23** : `@GET("v3/VOTRE_MOCK_ID") Call<List<JobOffer>> getJobOffers()`
  - 📄 `app/src/main/java/com/example/jobboard/api/RetrofitClient.java`
    - Ligne **4** : import `GsonConverterFactory`
    - Ligne **32** : `private static final String BASE_URL = "https://run.mocky.io/"`
    - Lignes **34–41** : `Retrofit.Builder` avec `GsonConverterFactory.create()` (ligne **37**)
  - 📄 `app/src/main/java/com/example/jobboard/fragments/OffresFragment.java`
    - Ligne **75** : `call.enqueue(new Callback<List<JobOffer>>() {...})` — appel asynchrone

- [x] **Notifications :** Utilisation de `NotificationCompat.Builder` et création obligatoire d'un `NotificationChannel` (Android 8+).
  - 📄 `app/src/main/java/com/example/jobboard/utils/NotificationHelper.java`
    - Ligne **10** : `import androidx.core.app.NotificationCompat`
    - Lignes **27–43** : `createNotificationChannel()` — `new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, IMPORTANCE_DEFAULT)`
    - Lignes **49–70** : `sendApplicationNotification()` — `new NotificationCompat.Builder(context, CHANNEL_ID)` avec `.setSmallIcon()`, `.setContentTitle()`, `.setStyle(BigTextStyle)`, `.setAutoCancel(true)`
  - 📄 `app/src/main/java/com/example/jobboard/MainActivity.java`
    - Ligne **45** : `NotificationHelper.createNotificationChannel(this)` — initialisation au lancement

- [x] **AsyncTask (Note d'Architecture) :** *Conformément aux bonnes pratiques modernes vues en atelier, `AsyncTask` étant officiellement déprécié par Google, l'asynchronisme réseau a été géré de manière plus robuste et native via la méthode `.enqueue()` de Retrofit, évitant ainsi les fuites de mémoire et les exceptions sur le thread principal.*
  - 📄 `app/src/main/java/com/example/jobboard/fragments/OffresFragment.java`
    - Ligne **75** : `call.enqueue(...)` — le callback `onResponse` / `onFailure` s'exécute automatiquement sur le **thread principal** grâce à Retrofit, sans `AsyncTask`, sans `runOnUiThread`, et sans risque de fuite mémoire.

---

## 🗂️ Structure du Projet

```
JobBoard/
├── app/src/main/
│   ├── AndroidManifest.xml
│   ├── assets/
│   │   └── job_offers.json                  ← 5 offres JSON réalistes
│   ├── java/com/example/jobboard/
│   │   ├── MainActivity.java                ← Navigation + Header + Notifications
│   │   ├── DetailActivity.java              ← Carte + Photo + Postuler + Appel/SMS
│   │   ├── adapters/
│   │   │   ├── JobAdapter.java              ← RecyclerView offres
│   │   │   └── ApplicationAdapter.java      ← RecyclerView candidatures + suppression
│   │   ├── api/
│   │   │   ├── JobApiService.java           ← Interface Retrofit (@GET)
│   │   │   └── RetrofitClient.java          ← Singleton Retrofit + GsonConverter
│   │   ├── database/
│   │   │   └── DatabaseHelper.java          ← SQLiteOpenHelper (CRUD candidatures)
│   │   ├── fragments/
│   │   │   ├── OffresFragment.java          ← Liste offres + fallback assets
│   │   │   ├── CandidaturesFragment.java    ← Historique + suppression
│   │   │   └── PreferencesFragment.java     ← Mots-clés SharedPreferences + Chips
│   │   ├── models/
│   │   │   ├── JobOffer.java                ← Modèle offre (id, title, company, location, description)
│   │   │   ├── Application.java             ← Modèle candidature (BDD)
│   │   │   └── Post.java                    ← Modèle JSONPlaceholder (legacy)
│   │   └── utils/
│   │       └── NotificationHelper.java      ← Canal + envoi notification locale
│   └── res/
│       ├── drawable/
│       │   ├── badge_waiting.xml            ← Badge orange "En attente"
│       │   ├── edit_text_background.xml     ← Style EditText arrondi
│       │   ├── header_gradient.xml          ← Dégradé bleu header MainActivity
│       │   └── ic_offres/candidatures/...   ← Icônes BottomNav
│       ├── layout/
│       │   ├── activity_main.xml            ← Header Job/Board + FragmentContainer + BottomNav
│       │   ├── activity_detail.xml          ← Détail offre (carte, boutons, description)
│       │   ├── fragment_offres.xml
│       │   ├── fragment_candidatures.xml
│       │   ├── fragment_preferences.xml     ← TextInputLayout + ChipGroup
│       │   ├── item_job_offer.xml           ← Item RecyclerView offre (CardView)
│       │   └── item_application.xml         ← Item RecyclerView candidature (CardView + 🗑️)
│       ├── menu/
│       │   └── bottom_nav_menu.xml          ← 3 items BottomNavigationView
│       ├── values/
│       │   ├── colors.xml                   ← Couleurs JobBoard (jb_primary, jb_accent...)
│       │   ├── strings.xml
│       │   └── themes.xml                   ← Theme.Material3.DayNight.NoActionBar
│       └── xml/
│           ├── file_paths.xml               ← FileProvider paths (photos CV)
│           ├── backup_rules.xml
│           └── data_extraction_rules.xml
└── gradle/
    └── libs.versions.toml                   ← Versions : Retrofit 2.11, Maps 19.2, Material 1.14...
```

---

## 🛠️ Instructions d'installation

1. Cloner le dépôt Git.
2. Ouvrir le projet avec Android Studio.
3. Renseigner la clé API Google Maps dans le fichier `AndroidManifest.xml`
   (`android:value="YOUR_MAPS_API_KEY"` — ligne **30**).
   > Pour héberger les offres sur **Mocky.io** : coller le contenu de `assets/job_offers.json`,
   > générer l'URL, puis mettre à jour `RetrofitClient.java` (ligne **32**) et `JobApiService.java` (ligne **23**).
4. Lancer la synchronisation Gradle (`File > Sync Project with Gradle Files`).
5. Déployer sur un émulateur ou un appareil physique (**Android 8.0+ recommandé** pour les Notification Channels).

