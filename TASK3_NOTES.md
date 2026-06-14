# Task 3 - RecyclerView avec Retrofit API

## 📦 Structure créée:

### 1. **Modèles (Models)**
- **JobOffer.java** - Classe modèle avec fields: id, title, company, location
- **Post.java** - Classe pour mapper la réponse JSONPlaceholder API

### 2. **API (Retrofit)**
- **JobApiService.java** - Interface avec call GET asynchrone `getJobOffers()`
- **RetrofitClient.java** - Gestionnaire d'instance Retrofit singleton
  - Base URL: `https://jsonplaceholder.typicode.com/`
  - Converter: GsonConverterFactory

### 3. **Recycle View & Adapter**
- **JobAdapter.java** - RecyclerView Adapter
  - ViewHolder pour chaque item
  - Interface OnJobClickListener pour gérer les clics
  - Méthode notifyDataSetChanged() pour mettre à jour la liste

### 4. **Fragments**
- **OffresFragment.java** - Affiche la liste des offres
  - Initialise RecyclerView avec LinearLayoutManager
  - Appel asynchrone via Retrofit Callback
  - Gestion ProgressBar pendant le chargement
  - Gestion des erreurs réseau

### 5. **Layouts XML**
- **fragment_offres.xml** - FrameLayout avec RecyclerView + ProgressBar + ErrorTextView
- **item_job_offer.xml** - CardView pour chaque offre (titre, entreprise, localisation)

### 6. **Dépendances ajoutées**
- ✅ CardView 1.0.0 (pour les items stylisés)
- ✅ Fragment 1.7.0
- ✅ RecyclerView 1.3.2
- ✅ Retrofit 2.11.0 + Gson Converter

### 7. **Permissions**
- ✅ INTERNET permission dans AndroidManifest.xml

## 🔄 Flux de données:

1. **OffresFragment.onViewCreated()** → Initialise RecyclerView
2. **fetchJobOffers()** → Appel API asynchrone via Retrofit
3. **Call.enqueue(Callback)** → Récupère List<Post> de JSONPlaceholder
4. **Conversion** → Post → JobOffer (ajout company, location générés)
5. **jobAdapter.setJobOffers()** → notifyDataSetChanged()
6. **RecyclerView** → Affiche 10 premières offres dans CardView

## 📡 API utilisée:
- JSONPlaceholder: `https://jsonplaceholder.typicode.com/posts`
- Format: [{ userId, id, title, body }, ...]

## 🎯 Résultat attendu:
✅ OffresFragment affiche une liste défilante avec les offres d'emploi
✅ Chaque offre est dans une CardView avec:
   - Titre (from title)
   - Entreprise (from userId)
   - Localisation (générée)
✅ ProgressBar pendant le chargement
✅ Gestion des erreurs réseau
✅ Clics sur les offres affichent un Toast

## 🧪 Test:
1. Lancer l'app
2. Aller dans l'onglet "Offres"
3. Observer le ProgressBar puis la liste apparaître
4. Scroller la liste
5. Cliquer sur une offre → Toast

