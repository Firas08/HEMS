# Configuration JAVA_HOME pour Karaf

## Problème Actuel

Karaf ne démarre pas car JAVA_HOME pointe vers un chemin invalide :
`C:\Program Files\Common Files\Oracle\Java\Java`

## Solution

### Étape 1 : Trouver le bon chemin Java

Ouvrez PowerShell et tapez :
```powershell
where.exe java
```

Ou vérifiez dans :
```
C:\Program Files\Java\
```

Vous devriez voir un dossier comme `jdk-17` ou `jdk-21`.

### Étape 2 : Définir JAVA_HOME

**Option A - Temporaire (pour ce terminal uniquement)** :
```batch
set JAVA_HOME=C:\Program Files\Java\jdk-17
```
(Remplacez `jdk-17` par votre version)

**Option B - Permanent (recommandé)** :
1. Clic droit sur "Ce PC" → Propriétés
2. Paramètres système avancés
3. Variables d'environnement
4. Nouvelle variable système :
   - Nom : `JAVA_HOME`
   - Valeur : `C:\Program Files\Java\jdk-17` (votre chemin)
5. OK → OK → OK
6. **Redémarrer le terminal**

### Étape 3 : Lancer Karaf

Une fois JAVA_HOME configuré :
```batch
cd karaf\bin
karaf.bat
```

Ou utilisez le script automatique que je vais créer.
