# Android aplikacija Filmovil

U ovom radu opisan je način funkcionisanja android aplikacije za prikaz liste popularnih filmova. Aplikacija je rađena u Android Studiu u java programskom jeziku. Razvijen je sistem za prijavljivanje, registraciju, verifikaciju putem emaila, reset lozinke. Za prikaz filmova korišten je besplatan API sa sajta www.themoviedb.org. Za čuvanje podataka (korisnika koji će se prijavljivati) korištena je online baza podataka firebase.

Struktura aplikacije sastoji se od tri glavna foldera:

* Manifest folder sa AndroidManifest.xml fajlom koji pruža osnovne podatke o aplikaciji.
* Java folder koji sadrži klase naše aplikacije (back end naše aplikacije).
* Res folder sadrži front end deo naše aplikacije.

## Front-end aplikacije

Front end deo naše aplikacije nalazi se u direktorijumu res u hijerarhiji projekata aplikacije. Predstavja kolekciju fajlova resursa kao što su znakovni nizovi, slike, fontovi i boje koje se prikazuju u korisničkom interfejsu, zajedno sa XML reprezentacijom rasporeda korisničkog interfejsa.

### Layout korisničkog interfejsa

Svaki od _activity_ i _fregment_ komponenata ima svoj layout koji će biti prikazani u narednom tekstu i svoju klasu u java folderu koja će biti objašnjena kasnije u tekstu.

__1. activity_login.xml__

Sadrzi dva _TextView elementa_ koji se odnose na naslov i podnaslov naše aplikacije, dva _EditText_ polja za email i šifru, _button_ elementa za prijavu, dva _TextView_ elementa za zaboravljena lozinka i registruj se i _ProgressBar_ elementa koji je vidljiv samo u trenutku dok se korisnik prijavljuje. Interfejs za prijavljivanje prikazan je na narednoj slici.

![Prijavljivanje](https://user-images.githubusercontent.com/98473818/152945897-f6638ec7-0acc-40c3-9455-f1d2d807523f.png)

__2. activity_register.xml, activity_forgot_pass.xml__

Sastoje se od istih elemenata kao i prethodno opisana aktivnost, a njihov izgled prikazan je na sledećoj slici.

![Registracija-zaboravljena lozinka](https://user-images.githubusercontent.com/98473818/152946336-ea374fce-1b6d-4680-8d9f-6de07aeffade.png)

__3. activity_profile.xml__

Služi za prikaz podataka iz realtime baze, prikazan je na sledećoj slici.

![Profil](https://user-images.githubusercontent.com/98473818/152946837-d94c0aa3-4469-42c1-a2a5-ce246b2c089a.png)

__4. fragment_home.xml, movie_item.xml__

Sastoji se od _ImageView_ i _RecyclerView_ elementa. _ImageView_ je obična slika odnosno naš baner. _RecyclerView_ sadrži _placeholdere_ (item 0 – item 9), a _itemi_ u našem slučaju su filmovi. Pošto i naši filmovi moraju da imaju dizajn, način na koji se prikazuje definisan je sa _movie_item.xml_ datotekom koja se prikazuje u _RecyclerView_ u rasporedu jedna ispod druge. Prikaz je dat na narednoj slici.

![Pocetna](https://user-images.githubusercontent.com/98473818/152947846-75aa037b-5a8e-40d4-83cb-ef4ca8d3ebd7.png)

### Meni korisničkog interfejsa

Imamo dva menija i to u _bottom_nav.xml_ i _options_menu.xml_. Obe .xml datoteke renderuje se na identičan način, ali se ne koriste isto. _Bottom_nav.xml_ ima dve opcije (početna i omiljena) i prikazuju se na dnu aplikacije u fragmentima._Options_menu.xml_ nudi dve opcije (profil i izloguj se) gde profil učitava _profile_ aktivnost za prikaz podataka iz baze, a klikom na izloguj se vraćamo je na stranicu za prijavljivanje. Prikaz je dat na sledećoj slici.

![Meni](https://user-images.githubusercontent.com/98473818/152948704-df9e9038-a086-4764-b93f-c8266f279ae1.png)

#### Ostali folderi u direktorijumu /res

Preostali folderi /res direktorijuma jesu _mipmap_, _values_, _drawable_. U _mipmap_ definisana je ikonica launčera, _values_ definiše boje, pozadinu. U folderu _drawable_ definisane su sve slike (ikonica, baner).

## Back-end aplikacije

U ovom poglavlju objasnićemo kako aplikacija radi objašnjenjem svake klase pojedinačno.

### Login klasa

Svaki element koji se nalazi u front end delu aplikacije mora da se nalazi i u klasi. Definišemo tip i ime. Pošto za autentifikaciju koristimo _firebase_ deklarišemo ime objekta za autentifikaciju _mAuth_.

```Java
    private EditText mEmail;
    private EditText mPassword;
    private Button mSignIn;
    private TextView mForgotPassword;
    private TextView mRegister;

    private ProgressBar mProgressBar;

    //Firebase...
    private FirebaseAuth mAuth;
```
</br>
Prva metoda _onCreate_ kreira se objekat klase i podešava se da se klasa _Login.java_ povezuje sa _activity_login.xml_, nakon čega pravimo instancu na objekat mAuth(). Na kraju pozivamo metodu login() koja prijavljuje korisnika.

```Java
@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        login();
    }
```
</br>
Nakon metode _onCreate_ kreiramo metodu _login()_ gde u prvom delu koda povezujemo elemente sa frontend delom apliakcije preko id-a.

```Java
private void login() {

        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mSignIn = findViewById(R.id.signIn);
        mForgotPassword = findViewById(R.id.forgotPassword);
        mRegister = findViewById(R.id.register);
        mProgressBar = findViewById(R.id.progressBar);
```
</br>
Nakon povezivanja elemenata programiramo dugme za prijavljivanje pomoću metode _setOnClickListener_. Zatim izvlačimo podatke iz _mEmail_ i _mPassword_ i smeštamo ih u posebne promenljive.

```Java
mSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String pass = mPassword.getText().toString().trim();
```
</br>
Sledeće što radimo jeste validacija tako što tražimo da korisnik unese polja za _email_ i _password_ pozivom metode _setError_ na front endu gde će biti obavešten da je polje obavezno ukoliko ne unese podatke, a zatim se sa _requestFocus_ korisnik odma fokusira na polje koje treba da popuni. Vrši se provera da li je email ukucan u pravilnoj formi.
Kada se sve pravilno unese progresbar se stavlja na visible dok se korisnik prijavljuje.

```Java
 if(TextUtils.isEmpty(email)){
                    mEmail.setError("Obavezno polje...");
                    mEmail.requestFocus();
                    return;
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    mEmail.setError("Unesite pravilan email!");
                    mEmail.requestFocus();
                    return;
                }
                if(TextUtils.isEmpty(pass)){
                    mPassword.setError("Obavezno polje...");
                    mPassword.requestFocus();
                    return;
                }


                mProgressBar.setVisibility(View.VISIBLE);
```
</br>
Nakon validacije programiramo prijavu korisnika pozivom objekta _mAuth_ i koristimo metodu _signInWithEmailAndPassword(email, pass)_ gde šaljemo _email_ i _password_ i zatim dodajemo _addOnCompleteListener_ nakon kog se izgeneriše _onComplete_ metoda prikazana na narednoj slici.

```Java
mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
```
</br>
Prvo proveravamo da li se korisnik uspešno prijavio. Ukoliko je prijava uspešna proverava se da li je korisnik verifikovan. Ako je korisnik verifikovan _progresbar_ se sklanja i pokreće se _MainActivity_ klasa. Ukoliko korisnik nije verifikovan _progressbar_ se sklanja i šalje se korisniku verifikacija na email i poruka da proveri svoj email. Ukoliko je korisnik uneo pogrešne podatke dobiće obaveštenje da je prijava neuspešna.

```Java
if(task.isSuccessful()){

FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                           if (user.isEmailVerified()){
                                mProgressBar.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(),"Uspesno logovanje",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            } else {
                                mProgressBar.setVisibility(View.GONE);
                                user.sendEmailVerification();
                                Toast.makeText(getApplicationContext(),"Proverite Vas email!",Toast.LENGTH_LONG).show();
                            }


                        } else {
                            mProgressBar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(),"Neuspesno logovanje",Toast.LENGTH_SHORT).show();
                        }
```
</br>
Pošto ispod dugmeta za uloguj se imamo dva dugmeta za zaboravljenu lozinku i registraciju potrebno je i njih programirati na sledeći način: Postavljaju se listener-i koji će na klik učitati klasu _Register_ ili _ForgetPass_ u zavisnoti šta korisnik izabere.

```Java
//Pokretanje prozora za registraciju
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),Register.class));
            }
        });

        //Pokretanje prozora za reset lozinke
        mForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),ForgotPass.class));
            }
        });
```
</br>

### Register klasa

Klasa za registraciju je u velikoj meri slična sa klasom _login_. Definišemo tip i ime za svaki element u frontu i definišemo _firebase_ objekat. Zatim se kreira _onCreate_ metoda i instanca firebase objekta nakon čega pozivamo metodu za registraciju-

```Java
 //Definisanje elemenata na frontu
    private TextView mNaslov;
    private EditText mName;
    private EditText mAge;
    private EditText mEmail;
    private EditText mPassword;
    private Button mRegister;

    private ProgressBar mProgressBar;

    //Firebase objekat
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Dobijanje instance Firabase objketa
        mAuth = FirebaseAuth.getInstance();
        //Metoda ya registraciju
        registration();
    }
```
</br>
U metodi za registraciju povezujemo elemente sa front end delom preko metode _findViewById_.

```Java
//Povezivanje komponneti sa frontom
        mNaslov = findViewById(R.id.naslov);
        mName = findViewById(R.id.name);
        mAge = findViewById(R.id.age);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mRegister = findViewById(R.id.register);
        mProgressBar = findViewById(R.id.progressBar);
```
</br>

Zatim programiramo dugme za registraciju pomoću metode _setOnClickListener_ nakon čega u metodi _onClick_ izvlačimo podatke koje proveravamo da li su pravilni.

```Java
  //Programiranje dugmeta za registaciju
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Izvlacenje podataka iz komponenti
                String name = mName.getText().toString().trim();
                String age = mAge.getText().toString().trim();
                String email = mEmail.getText().toString().trim();
                String pass = mPassword.getText().toString().trim();

                //Provera da li su podaci pravilni
                if(TextUtils.isEmpty(name)){
                    mName.setError("Obavezno polje...");
                    mName.requestFocus();
                    return;
                }
                if(TextUtils.isEmpty(age)){
                    mAge.setError("Obavezno polje...");
                    mAge.requestFocus();
                    return;
                }
                if(TextUtils.isEmpty(email)){
                    mEmail.setError("Obavezno polje...");
                    mEmail.requestFocus();
                    return;
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    mEmail.setError("Unesite pravilan email!");
                    mEmail.requestFocus();
                    return;
                }
                if(TextUtils.isEmpty(pass)){
                    mPassword.setError("Obavezno polje...");
                    mPassword.requestFocus();
                    return;
                }

                //Stavljamo progresbar da se vrti
                mProgressBar.setVisibility(View.VISIBLE);
```
</br>
Nakon što je dugme programirano sledeće na redu jeste programiranje kreiranja korisnika.Vrši se pozivom objekta _mAuth_ i metode _createUserWithEmailAndPassword_. Proveravamo da li je korisnik uspešno registrovao i zatim njegove podatke čuvamo u poseban objekat koji ćemo proslediti u našu bazu. Provera da li se korisnik uspešno kreirao i pravimo objekat korisnika _user_.

```Java
//Programiramo kreiranje korisnika
                mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        //Proveramo da li je korinsik kreiran
                        if(task.isSuccessful()){

                            //Pravimo objekat korisnika
                            User user = new User(name,age,email);
```
</br>

U klasi _user_ čuvamo podatke o korisniku (name, age, email)

```Java
//Klasa koja cuva podatke o korisniku
public class User {

    public String name,age,email;

    public User(){

    }

    public User(String name,String age,String email){
        this.name = name;
        this.age = age;
        this.email = email;
    }

}

```
</br>

Zatim šaljemo objekat korisnika u online bazu podataka odnosno šalje se trenutni korisnik koji je registrovan i vrednost _user_ koji sadrži _name, age, email_

```Java
//Objekat korisnika saljemo u realtime onlina bazu podataka
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user);
```
</br>
Gasimo progresbar i obaveštavamo korisnika da je registracija uspešna nakon čega se prebacuje na login prozor, a u slučaju greške obaveštavamo korisnika da je registracija neuspešna.

```Java
//Gasimo progresbar i ispisujemo toast poruku
                            mProgressBar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(),"Uspesna registracija",Toast.LENGTH_SHORT).show();

                            //Prebacujemo korisnika na login prozor
                            startActivity(new Intent(getApplicationContext(),Login.class));
                        } else {
                            mProgressBar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(),"Neuspesna registracija",Toast.LENGTH_SHORT).show();
                        }
```
</br>

Na kraju ukoliko korisnik klikne na ime aplikacije, prebacuje se na login prozor

```Java
 //Ako korisnik klikne na ime aplikacije, prebacujemo ga na login prozor
        mNaslov.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),Login.class));
            }
```
</br>

### ForgotPass klasa

Povezuju se elementi na front endu i kreira se _firebase_ objekat nakon čega se kreira _onCreate_ metoda u kojoj smo odma vršili povezivanje elemenata sa front dela i pozivamo instancu firebase objekta i programiramo šta se dešava klikom na dugme _reset password_ (pokreće se metoda _resetPassword_).

```Java
private EditText mEmail;
    private Button mResetPassword;

    private ProgressBar mProgressBar;

    //Firebase...
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);

        mEmail = findViewById(R.id.email);
        mResetPassword = findViewById(R.id.resetPassword);
        mProgressBar = findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();

        mResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });
    }
```
</br>

U metodi _resetPassword_ uzimamo email sa front dela i proverava se da li je unet ispravno.

```Java
    private void resetPassword() {

        String email = mEmail.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            mEmail.setError("Obavezno polje...");
            mEmail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            mEmail.setError("Unesite pravilan email!");
            mEmail.requestFocus();
            return;
        }
```
</br>

Pozivamo _mAuth_ objekat i poziva se _sendPasswordResetEmail(email)_ nakon čega se generiše _onComplete_ metoda. U metodi onComplete provera se da li je link za reset uspešno poslat i ako jeste obaveštava se korisnik da proveri email, a u suprotnom izbacuje se poruka greška.

```Java
        mProgressBar.setVisibility(View.VISIBLE);
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    mProgressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(),"Proverite Vas email!",Toast.LENGTH_SHORT).show();
                } else {
                    mProgressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(),"Greska!",Toast.LENGTH_SHORT).show();
                }
            }
        });
```
</br>

### MainActivity klasa

Prva metoda na koju nailazimo jeste _onCreate()_ metoda u kojoj na početku definišemo da se klasa _MainActivity.java_ povezuje sa _activity_main.xml_. Pošto imamo bottom navigaciju potrebno je definisati _bottomNavigationView_ objekat koji povezujemo sa front delom(bottom_navigation). Zatim postavljamo _listener_ jer želimo da korisnik kada klikne na neki od itema u navigaciji da se nešto dešava.

```Java
 protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
```
</br>

Sledi metoda _onNavigationItemSelected_ koja sadrži objekat _item_. Objekat _item_ dobijamo kada korisnik klikne na određeni item. Zatim pravimo jedan _switch_ gde u zavisnosti od id-a koji je kliknut (nav_home ili nav_favorite) mi želimo da na prazan fragment koji smo napravili od njega napravimo novi _Home ili FavoriteFragment_. Nakon što znamo šta je korisnik izabrao pozivamo _getSupportFragmentManager()_ i pokrećemo transakciju između naša dva fragmenta i pozivamo _replace_ gde menjamo _fragment_container_ sa selektovanim fragmentom i na taj način vršimo prelaz između fregmenata.

```Java
private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selFragment = null;

                    switch (item.getItemId()){
                        case R.id.nav_home:
                            selFragment = new HomeFragment();
                            break;
                        case R.id.nav_favorite:
                            selFragment = new FavoriteFragment();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selFragment).commit();
                    return true;
                }
            };
```
</br>

U _MainActivity_ klasi ostale su nam još dve metode. Prva metoda jeste _onCreateOptionsMenu_ koja služi za kreiranje menija.

```Java
//Metoda koja kreira meni
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.options_menu,menu);
        return true;
    }
```
</br>
Druga metoda služi za praćenje na koji item je kliknuto u meniju. Imamo _switch_ u kome ako je kliknuto na _profile_ otvara se klasa _Profile_, a ukoliko je kliknuto na _Logout_ prvo odjavi korisnika, a zatim ga vrati na login klasu.

```Java
//Metoda koja prati na koji ITEM je kliknuto u meniju
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profile:
                startActivity(new Intent(getApplicationContext(), Profile.class));
                return true;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), Login.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
```
</br>

### Profile klasa

Prva stvar koju radimo jeste definisanje elemenata koji se nalaze u front delu, a to su: _TextView_ elementi, _FirebaseUser_ za našeg user-a, _DatabaseReference_ jer korisitmo našu bazu i _userID_ gde čuvamo id korisnika.

```Java
    private TextView mGret, mEmailAddressTitle, mEmailAddress, mNameTitle, mName, mAgeTitle, mAge;

    private FirebaseUser user;
    private DatabaseReference reference;

    private String userID;
```
</br>

Zatim imamo metodu _onCreate_ u kojoj povezujemo elemente preko id.

```Java
protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mGret = findViewById(R.id.gret);
        mEmailAddressTitle = findViewById(R.id.emailAddressTitle);
        mEmailAddress = findViewById(R.id.emailAddress);
        mNameTitle = findViewById(R.id.nameTitle);
        mName = findViewById(R.id.name);
        mAgeTitle = findViewById(R.id.ageTitle);
        mAge = findViewById(R.id.age);
```
</br>

Kreiramo korisnika, pravimo referencu na bazu i uzimamo id pomoću _user.getUId();_

```Java
 user = FirebaseAuth.getInstance().getCurrentUser();
 reference = FirebaseDatabase.getInstance().getReference("Users");
 userID = user.getUid();
```
</br>

Podatke učitavamo tako što koristimo promenljivu reference, pristupamo _child_ metodu gde navodimo _userID_ i dodajemo _listener_ nakon čega se definiše metoda _onDataChange_ i _onCanceled_. U metodu onDataChange uzimamo _snapshot_(koje sve komponente se nalaze u klasi) klase _user_ user(pravimo _userProfile_ i uzimamo _snapshot_). Ukoliko _userProfile_ nije null onda se podešava _name, age, email_. Objekat userProfile dobija podatke iz snapshota koji dolazi iz metode _onDataChange_. Nakon što su _name, age, email_ podešeni na _userProfile_ koji je stigao iz snapshota vršimo povezivanje sa front delem.

```Java
reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if (userProfile != null){
                    String name = userProfile.name;
                    String age = userProfile.age;
                    String email = userProfile.email;

                    mGret.setText("Dobrodosao, " + name + "!");
                    mName.setText(name);
                    mAge.setText(age);
                    mEmailAddress.setText(email);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),"Greska!!!",Toast.LENGTH_SHORT).show();
            }
        });
```
</br>

### Učitavanje podataka sa interneta

Podaci sa interneta učitavaju se u _fragment_home.xml_ tako što _RecyclerView_ punimo sa movie elementima čiji raspored je definisan u _movie_item.xml_. Prva klasa koju ćemo objasniti jeste _MovieModelClass.java_ koja se nalazi u folderu model. Opisuje šta se nalazi u filmu koji skidamo a to su njegov _id, name, image_. Definišemo konstruktor i setere i getere.

```Java
public class MovieModelClass {

    String id, name, image;

    public MovieModelClass(String id, String name, String image) {
        this.id = id;
        this.name = name;
        this.image = image;
    }

    public MovieModelClass() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}

```
</br>

U klasi HomeFragment.java skidamo podatke. Prvo idemo na API koji smo preuzeli, odnosno preko njega dobijamo podatke. Zatim definišemo listu naših filmova koje skidamo i kreiramo jedan _recyclerView_.

```Java
    private static String JSON_URL = "https://api.themoviedb.org/3/movie/popular?api_key=429cdcc9e17c65e735150d1bce79dd89";

    List<MovieModelClass> movieList;
    RecyclerView recyclerView;
```
</br>

Pošto je ovo fragment logiku ne možemo da radimo u metodi _onCreateView_ već pravimo posebno metodu koja se zove _onViewCreated()_. U toj metodi pravimo _movieList_ kao novi niz i _recyclerView_ povezujemo sa front delemo(_movieList koji se nalazi u fragment_home_). Kreiramo novu klasu _getData_ i pravimo objekat te klase naziva _getData_ iz kog pozivamo metodu _execute()_.

```Java
public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        movieList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.movieList);

        GetData getData = new GetData();
        getData.execute();
    }
```
</br>

Naša klasa _GetData_ proširuje klasu _AsyncTask_(klasa koja sluzi da skida podatke sa interneta). Klasa _AsyncTask_ mora da implementira metodu _doInBackground i onPostExecute_.
U metodi _doInBackground_ prvo pravimo url(novu adresu) i _HttpURLConnection_ u jednom _try_ bloku

```Java
 try {
                URL url;
                HttpURLConnection urlConnection = null;
```
</br>

Zatim url postavljamo na link koji smo na početku stavili(JSON_URL) i otvaramo konekciju na tom linku. Zatim otvaramo _stream_ i skidamo podatke i dokle god podaci nisu -1 znači da ih ima i skidamo ih. Podatke stavljamo u objekat _current_.

```Java
try {
                    url = new URL(JSON_URL);
                    urlConnection = (HttpURLConnection) url.openConnection();

                    InputStream is = urlConnection.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is);

                    int data = isr.read();
                    while (data != -1){
                        current += (char) data;
                        data = isr.read();
                    }
```
</br>

Kada smo završili učitavanje podataka i ubacili sve podatke u _current_ zatvaramo konekciju. Druga metoda _onPostExecute_ kreira novi _json_ objekat i novi _json_ niz. Iz niza koji se zove _results_ izvlače se podaci dokle god nije 0 i to podaci za _vote_average, title, poster_path koji idu u modelClass._ Kada se svi podaci dodaju u _movieList_ tu listu šaljemo u metodu _PutDataIntoRecyclerView_ koja prima tu listu i zatim _recyclerView_ puni tim podacima.

```Java
    private void PutDataIntoRecyclerView(List<MovieModelClass> movieList){

        MovieAdaptery adaptery = new MovieAdaptery(getContext(),movieList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerView.setAdapter(adaptery);

    }
```
</br>

Podaci se pune tako što se kreira adapter koji je _MovieAdaptery_ i u taj adapter se šalje lista. Poslednja klasa jeste _MovieAdaptery.java_ koja se koristi samo u metodi _PutDataIntoRecyclerView._ Sadrži metodu _onCreateViewHolder_ u kome se pravi pogled u kome _Layout_ punimo sa movie_item.

```Java
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;

        LayoutInflater inflater = LayoutInflater.from(mContext);
        v = inflater.inflate(R.layout.movie_item, parent, false);

        return new MyViewHolder(v);
    }
```
</br>

Zatim povezujemo podatke tako što setujemo _id i name_ holdera i koristimo biblioteku _glide_ koja učitava sliku.

```Java
    //Povezivanje podataka iz holdera
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.id.setText(mData.get(position).getId());
        holder.name.setText(mData.get(position).getName());

        //Koristimo Glide za prikaz slike
        Glide.with(mContext)
                .load("https://image.tmdb.org/t/p/w500"+mData.get(position).getImage())
                .into(holder.img);
```
</br>

Na kraju imamo klasu _MyViewHolder_ koja proširuje _ViewHolder_ iz _RecyclerView-a_ koja u sebi sadrži elemente sa front dela, koji se zatim u klasi _MyViewHolder_ povezuju.

```Java
public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView id,name;
        ImageView img;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            id = itemView.findViewById(R.id.id_txt);
            name = itemView.findViewById(R.id.name);
            img = itemView.findViewById(R.id.imageView);
        }
    }
```
</br>

Da sumiramo kako se podaci učitavaju: pozivom metode PutDataIntoRecyclerView u nju stiže lista json objekata koji se šalju u MovieAdaptery klasu gde se pretvaraju u prave podatke koji se šalju u recyclerView koji se prikazuju na aplikaciji.















