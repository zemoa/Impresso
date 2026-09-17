# IMP-09 — Signaler qu'une imprimante sélectionnée est indisponible

## 1. Contexte et objectif

Après la sélection d'une imprimante, celle-ci peut devenir inutilisable avant le
démarrage de la soumission. IMP-09 empêche alors l'envoi vers cette imprimante et
guide l'utilisateur vers une autre sélection.

L'objectif du MVP est de ne jamais utiliser une imprimante dont l'indisponibilité est
connue avant la soumission. La fonctionnalité reste locale et ne déclenche aucune
notification système.

## 2. Périmètre

### Inclus

- Contrôle de disponibilité au moment de l'action **Imprimer**.
- Utilisation des mises à jour de disponibilité fournies par IMP-03 pour désactiver
  immédiatement l'action **Imprimer**.
- Blocage de l'envoi lorsque la disponibilité est inconnue ou négative au moment du
  clic.
- Boîte de dialogue bloquante indiquant que l'imprimante n'est plus disponible.
- Affichage du nom de l'imprimante lorsque celui-ci est disponible.
- Action **Choisir une autre imprimante** renvoyant vers IMP-04.
- Annulation de la sélection courante lorsque l'imprimante devient indisponible.

### Dépendances

- IMP-03 fournit l'état observable de disponibilité et ses mises à jour.
- IMP-04 réalise la revalidation à l'action **Continuer**, conserve l'imprimante
  indisponible visible et non sélectionnable, et reçoit le retour de ce parcours.
- IMP-07 définit le démarrage de la soumission et prend le relais à partir de cette
  frontière.
- Le mécanisme global de traduction de l'application fournit les versions anglaise
  et française des textes.

### Hors périmètre

- Découverte ou re-découverte des imprimantes.
- Nouvelle vérification périodique indépendante d'IMP-03.
- Réessai avec la même imprimante.
- Sélection ou gestion des imprimantes, qui appartiennent à IMP-04.
- Configuration avancée, soumission effective et suivi du job.
- Problèmes signalés après acceptation du job, qui appartiennent à IMP-11.
- Notifications système, cloud, Internet, comptes et impression distante.
- Wi-Fi Direct.

## 3. Personae et rôles

- **Utilisateur particulier :** sélectionne une imprimante et tente d'imprimer.
- **IMP-03 :** fournit les mises à jour de disponibilité.
- **IMP-04 :** possède la sélection d'imprimante et permet d'en choisir une autre.
- **IMP-07 :** possède la soumission à partir du moment où elle a démarré.
- **IMP-11 :** possède les problèmes rapportés après acceptation du job.

## 4. User stories

- En tant qu'utilisateur, je veux être empêché d'utiliser une imprimante indisponible
  afin de ne pas perdre de temps ou tenter un envoi voué à l'échec.
- En tant qu'utilisateur, je veux comprendre pourquoi l'impression est bloquée afin de
  pouvoir choisir une autre imprimante.

## 5. Parcours fonctionnels

### Parcours nominal

1. L'utilisateur arrive sur l'écran d'impression avec un fichier valide et une
   imprimante sélectionnée.
2. L'imprimante est signalée comme disponible.
3. L'action **Imprimer** est active.
4. L'utilisateur appuie sur **Imprimer**.
5. IMP-09 vérifie que la disponibilité est positive.
6. Le contrôle étant positif, IMP-07 peut démarrer la soumission.

### Imprimante devenue indisponible avant le clic

1. IMP-03 signale que l'imprimante n'est plus disponible.
2. La sélection courante est annulée.
3. L'action **Imprimer** est désactivée.
4. L'utilisateur revient à IMP-04 pour choisir une autre imprimante.

### Indisponibilité détectée au clic

1. L'utilisateur appuie sur **Imprimer** alors que l'état est négatif ou inconnu.
2. La soumission est bloquée.
3. Une boîte de dialogue bloquante affiche un message simple et le nom de
   l'imprimante s'il est disponible.
4. L'utilisateur choisit **Choisir une autre imprimante**.
5. Impresso revient à IMP-04.

## 6. Règles métier

1. Une imprimante connue comme indisponible ne peut pas être utilisée.
2. Une disponibilité inconnue est traitée comme non autorisée pour l'envoi.
3. La disponibilité fournie par IMP-03 peut désactiver **Imprimer** avant toute action
   utilisateur.
4. Une vérification est effectuée au clic sur **Imprimer**.
5. Si cette vérification échoue, aucun envoi n'est démarré par IMP-07.
6. La boîte de dialogue est bloquante et propose **Choisir une autre imprimante**.
7. L'indisponibilité annule la sélection courante.
8. L'imprimante indisponible reste visible dans IMP-04, mais n'est pas sélectionnable.
9. Le retour vers IMP-04 ne lance pas automatiquement une nouvelle découverte.
10. IMP-09 s'applique jusqu'au démarrage de la soumission. Après cette frontière, IMP-07
    et IMP-11 s'appliquent selon leurs propres règles.
11. Aucune notification système n'est envoyée.
12. Aucun document ou état d'imprimante n'est transmis hors du réseau local.

## 7. Cas nominaux, alternatifs et erreurs

### Cas nominaux

- L'imprimante est disponible au clic : l'envoi est transmis à IMP-07.
- L'imprimante devient indisponible avant le clic : l'action est désactivée et la
  sélection est annulée.

### Cas alternatifs

- Le nom de l'imprimante est absent : le message est affiché sans nom.
- L'utilisateur choisit une autre imprimante dans IMP-04 : le parcours reprend selon
  les règles de cette feature.
- Une nouvelle imprimante devient indisponible : le même comportement s'applique.

### Cas d'erreur

- Disponibilité négative au clic : soumission refusée et boîte de dialogue affichée.
- Disponibilité inconnue au clic : soumission refusée et même boîte de dialogue.
- Problème après démarrage de la soumission : hors IMP-09 ; voir IMP-07 et IMP-11.

## 8. Critères d'acceptation

- **Given** une imprimante est signalée indisponible avant le clic sur **Imprimer**,
  **When** l'état est reçu, **Then** **Imprimer** est désactivé et la sélection est
  annulée.
- **Given** une imprimante est indisponible, **When** l'utilisateur consulte IMP-04,
  **Then** elle reste visible, marquée indisponible et non sélectionnable.
- **Given** la disponibilité de l'imprimante est inconnue, **When** l'utilisateur
  appuie sur **Imprimer**, **Then** l'envoi est bloqué.
- **Given** la disponibilité est négative ou inconnue, **When** l'envoi est bloqué,
  **Then** une boîte de dialogue bloquante est affichée.
- **Given** la boîte de dialogue est affichée, **When** l'utilisateur choisit
  **Choisir une autre imprimante**, **Then** Impresso revient à IMP-04.
- **Given** le nom de l'imprimante est disponible, **When** la boîte de dialogue est
  affichée, **Then** le nom apparaît dans le message.
- **Given** l'imprimante est disponible, **When** l'utilisateur appuie sur
  **Imprimer**, **Then** IMP-09 autorise le handoff vers IMP-07.
- **Given** la soumission a démarré, **When** un problème est rapporté ensuite,
  **Then** IMP-09 ne redéfinit pas le comportement et IMP-11 prend le relais.
- **Given** une indisponibilité est détectée, **Then** aucune notification système,
  aucun compte et aucun service distant ne sont requis.

## 9. Données et états

### Données consommées

- Identifiant de l'imprimante sélectionnée.
- Nom de l'imprimante, lorsqu'il est disponible.
- État de disponibilité fourni par IMP-03.
- Résultat de la vérification au clic sur **Imprimer**.

### Données produites

- Sélection courante annulée en cas d'indisponibilité.
- Handoff autorisé vers IMP-07 ou refus d'envoi.
- Demande de retour vers IMP-04.

### États

- `available` : l'impression peut être tentée.
- `unavailable` : l'impression est bloquée, la sélection est annulée.
- `unknown` : l'impression est bloquée par prudence.
- `checking` : vérification en cours au clic sur **Imprimer**.
- `redirect-to-selection` : la boîte de dialogue est traitée et le retour vers IMP-04
  est demandé.
- `submission-started` : IMP-07 a pris le relais ; IMP-09 est terminé pour ce parcours.

## 10. Hors périmètre

La découverte, la sélection, la persistance et la nouvelle disponibilité des imprimantes
restent respectivement définies par IMP-03 et IMP-04. La transmission, le retry, le
suivi du job et les problèmes post-acceptation restent définis par IMP-07, IMP-10 et
IMP-11. Les réglages avancés, Wi-Fi Direct et les notifications système ne sont pas
ajoutés par IMP-09.

## 11. Intégration au produit

- **IMP-03 — dépendance :** expose les mises à jour d'état utilisées pour désactiver
  l'action.
- **IMP-04 — dépendance :** fournit la sélection initiale, reçoit le retour et conserve
  l'imprimante indisponible visible mais non sélectionnable.
- **IMP-07 — dépendance :** reçoit uniquement une imprimante dont le contrôle IMP-09 a
  autorisé le démarrage de soumission.
- **IMP-11 — hors périmètre :** traite les problèmes après acceptation du job.
- **Localisation globale — dépendance :** fournit les traductions des textes de la
  boîte de dialogue et de l'action.

## 12. Dépendances, handoffs et impacts fonctionnels

### Handoff entrant

IMP-09 reçoit d'IMP-04 une imprimante sélectionnée et l'état courant fourni par IMP-03.

### Handoff sortant

- Si la vérification est positive, IMP-09 autorise le passage vers IMP-07.
- Sinon, IMP-09 annule la sélection, affiche la boîte de dialogue et demande le retour
  vers IMP-04.

### Impacts fonctionnels

- IMP-03 doit rendre ses changements de disponibilité observables par l'écran
  d'impression.
- IMP-04 doit accepter le retour sans relancer automatiquement la découverte.
- IMP-07 doit distinguer l'absence d'autorisation avant soumission d'un échec survenu
  après son démarrage.
- Les textes doivent suivre la localisation globale de l'application.

## 13. Questions ouvertes et décisions à confirmer

### Décisions confirmées

- L'objectif est d'empêcher l'utilisation d'une imprimante inutilisable.
- Le contrôle est effectué au clic sur **Imprimer** et les contrôles IMP-04 sont
  conservés.
- L'état inconnu bloque l'envoi.
- La boîte de dialogue est bloquante et renvoie vers IMP-04.
- La sélection courante est annulée.
- Aucun retry avec la même imprimante ni aucune notification système n'est prévu dans
  le MVP.

### Questions ouvertes

Aucune question fonctionnelle bloquante.
