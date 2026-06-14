# Task 4 - Intents Explicites & Implicites

## ✅ Niveau Completed

### 📱 Composants créés:

#### **1. DetailActivity.java**
Une nouvelle Activity qui:
- Reçoit le `job_title` via Intent explicite
- Affiche le titre de l'offre d'emploi
- Contient 2 boutons: **Appeler** et **Envoyer SMS**

#### **Intent Explicite - JobAdapter → DetailActivity**
```java
Intent intent = new Intent(context, DetailActivity.class);
intent.putExtra("job_title", jobOffer.getTitle());
intent.putExtra("job_company", jobOffer.getCompany());
intent.putExtra("job_id", jobOffer.getId());
context.startActivity(intent);
```

#### **Intent Implicites dans DetailActivity**

**1️⃣ Appel téléphonique (ACTION_DIAL)**
```java
Intent dialIntent = new Intent(Intent.ACTION_DIAL);
dialIntent.setData(Uri.parse("tel:0612345678"));
startActivity(dialIntent);
```
- Lance l'app Dialer natif du téléphone
- Numéro pré-rempli: 0612345678
- User confirme l'appel manuellement

**2️⃣ SMS (ACTION_SENDTO)**
```java
Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
smsIntent.setData(Uri.parse("smsto:0612345678"));
smsIntent.putExtra("sms_body", "J'aimerais en savoir plus...");
startActivity(smsIntent);
```
- Lance l'app SMS natif du téléphone
- Numéro et message pré-remplis
- User envoie le SMS

### 🎨 UI - activity_detail.xml

**Layout structure:**
```
┌─────────────────────────────────┐
│ ← Retour  | Détails de l'offre │
├─────────────────────────────────┤
│ ┌─────────────────────────────┐ │
│ │ Titre du Poste:             │ │
│ │ [Job Title from Intent]    │ │
│ │                             │ │
│ │ Pour plus d'infos, contact. │ │
│ └─────────────────────────────┘ │
├─────────────────────────────────┤
│ ┌──────────────┐┌──────────────┐│
│ │☎ Appeler     ││💬 Envoyer SMS││
│ │le Recruteur  ││ un SMS       ││
│ └──────────────┘└──────────────┘│
├─────────────────────────────────┤
│ ┌─────────────────────────────┐ │
│ │📋 Instructions:             │ │
│ │ 1. Cliquer sur Appeler...   │ │
│ │ 2. Cliquer sur SMS...       │ │
│ │ 3. Utiliser apps natives... │ │
│ └─────────────────────────────┘ │
└─────────────────────────────────┘
```

### 📝 Modifications:

#### **JobAdapter.java**
- Ajout import: `DetailActivity`, `Intent`, `Context`
- Dans `JobViewHolder.bind()`:
  - Créé Intent explicite vers DetailActivity
  - Passage des données: job_title, job_company, job_id
  - Lancé via `context.startActivity(intent)`

#### **AndroidManifest.xml**
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.CALL_PHONE" />

<uses-feature
    android:name="android.hardware.telephony"
    android:required="false" />

<activity
    android:name=".DetailActivity"
    android:exported="false"
    android:parentActivityName=".MainActivity" />
```

### 🔄 Flux utilisateur:

1. **OffresFragment** → Liste des offres dans RecyclerView
2. User **clique sur une offre**
3. **Intent Explicite** → Ouvre DetailActivity
4. **DetailActivity** affiche les détails
5. User clique **"Appeler le Recruteur"** 
   - → **Intent Implicite (ACTION_DIAL)**
   - → Dialer app s'ouvre avec le numéro
6. OU User clique **"Envoyer un SMS"**
   - → **Intent Implicite (ACTION_SENDTO)**
   - → SMS app s'ouvre avec le numéro et message

### 🎯 Résultats attendus:

✅ RecyclerView items cliquables
✅ DetailActivity s'ouvre avec le titre passé via Intent
✅ Bouton "Appeler" → Dialer app avec numéro
✅ Bouton "SMS" → SMS app avec numéro et message
✅ Bouton "Retour" → Revenir à MainActivity
✅ Compilation réussie SANS erreurs

### 🧪 Tests:

1. Lancer l'app
2. Aller dans onglet "Offres"
3. **Cliquer sur une offre** → DetailActivity affiche le titre
4. **Cliquer sur "Appeler"** → Dialer s'ouvre avec le numéro pré-rempli
5. **Cliquer sur "SMS"** → SMS s'ouvre avec le message pré-rempli
6. **Cliquer sur "Retour"** → Revenir à la liste

### 💡 Concepts clés impliqués:

- ✅ **Intent Explicite**: Entre composants de l'app (DetailActivity)
- ✅ **Intent Implicite**: ACTION_DIAL pour appels téléphoniques
- ✅ **Intent Implicite**: ACTION_SENDTO pour SMS
- ✅ **putExtra()**: Passer données entre Activities
- ✅ **getIntent()**: Récupérer Intent et ses données
- ✅ **Permissions**: CALL_PHONE pour appels
- ✅ **Hardware Feature**: telephony required="false"

