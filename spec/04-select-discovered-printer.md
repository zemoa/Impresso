# IMP-04 — Select a Discovered Printer

## 1. Context and objective

### Context

After selecting a printable file, the user must choose the local printer that will
receive the document. The printer list is supplied by IMP-03. The user must be able to
identify printers quickly, avoid selecting an unavailable printer, and reuse printers
that have previously been used.

### Objective

Allow a user to select one active discovered printer, continue to the next step of the
standalone print flow, and maintain a durable list of printers that have reached the
start of document submission.

The default experience must remain direct: the last used registered printer is selected
automatically when it is discovered and active, but the user confirms the choice by
pressing **Continue**.

## 2. Scope

### Included

- Displaying printers supplied by IMP-03.
- Displaying available identifying information: name, model, network address, and
  availability state when provided.
- Dividing the list into **Registered printers** and **Other available printers**.
- Persisting multiple registered printers across application restarts.
- Selecting one active printer at a time.
- Automatically selecting the most recently used registered printer when it is found and
  active.
- Allowing the user to replace the automatic or manual selection.
- Sorting registered printers by most recent use and other printers alphabetically.
- Displaying inactive registered printers in a compact, less prominent presentation.
- Preventing selection of inactive printers.
- Rechecking availability when the user presses **Continue**.
- Registering the selected printer when document submission starts, including when the
  subsequent submission fails.
- Allowing the user to forget a registered printer after confirmation.
- Providing English and French user-facing strings, with English as the default language.
- Handing the selected printer to the next printing-flow step.

### Dependencies

- IMP-01 provides the valid selected file and its flow handoff.
- IMP-03 provides discovered printers, identifying data, availability state, progressive
  updates, and availability revalidation.
- The next printing-flow step accepts the selected printer and the selected file.
- The document-submission feature signals when submission starts so the selected printer
  can be registered.

### Out of scope

- Discovering printers or implementing discovery protocols.
- Wi-Fi Direct discovery or connection, which belongs to IMP-06.
- Adding a printer manually by address or any remote/cloud discovery.
- Configuring print settings.
- Sending, monitoring, or retrying a print job.
- Displaying a submission confirmation summary.
- System notifications for printer availability or selection changes.
- Print history, persistent queues, user accounts, or printer sharing.

## 3. Personas and roles

- **Home user:** reviews available printers, selects one, changes the selection when
  needed, and manages registered-printer preferences.
- **IMP-03:** supplies the discovered-printer collection and current observable
  availability.
- **Next print-flow step:** consumes the selected printer and selected file; it owns
  configuration and submission behavior.

## 4. User stories

- As a home user, I want to distinguish discovered printers so that I can choose the
  intended printer quickly.
- As a home user, I want my most recently used printer selected automatically so that I
  can continue with fewer actions.
- As a home user, I want to change the automatic selection so that I retain control over
  the destination printer.
- As a home user, I want unavailable printers to be visibly identified and unavailable
  for selection so that I do not submit to the wrong destination.
- As a home user, I want previously used printers to remain available after restarting
  the application so that I do not have to select them repeatedly.
- As a home user, I want to forget a registered printer so that obsolete preferences do
  not remain in my list.

## 5. Functional journeys

### 5.1 Opening the printer-selection screen

1. The user selects a valid file in IMP-01 and chooses **Continue**.
2. Impresso opens IMP-04.
3. Printers already supplied by IMP-03 are displayed immediately.
4. New discovery results may be added progressively while the screen is open.
5. If the most recently used registered printer is present and active, it is selected
   automatically when it becomes available.
6. The user may select another active printer.
7. The user presses **Continue** to hand off the file and selected printer.

If no registered printer is available, no automatic selection is made. The user chooses
an active printer from the available list.

### 5.2 List organization

The screen contains two sections:

1. **Registered printers**, shown first. Registered printers are ordered from most
   recently used to least recently used.
2. **Other available printers**, shown second. These printers are ordered alphabetically
   using their displayed identifying name; when no name is available, the applicable
   available identifying value is used.

An active registered printer is clearly marked as registered. An inactive registered
printer remains visible in the registered section but uses less space and visual
emphasis. An inactive printer supplied by discovery is visible according to the state
provided by IMP-03 and cannot be selected.

### 5.3 Changing the selection

The user may select any active printer. Only one printer can be selected at a time.

A manual selection replaces the automatic selection and remains the selected printer
while discovery continues. Later discovery results must not replace a manual selection.

### 5.4 Continuing

When the user presses **Continue**, IMP-04 requests a fresh availability check for the
selected printer. If the printer is still available, IMP-04 hands the selected printer
and the selected file to the next step.

If the printer is unavailable, IMP-04 keeps the user on the screen, updates the visible
state, and displays a localized message with the action **Choose another printer**.

The selected printer is registered when the next step begins document submission. This
registration occurs even if document submission subsequently fails.

### 5.5 Forgetting a printer

The user can choose **Forget** for a registered printer. Impresso requests confirmation
with **Cancel** and **Forget** actions.

After confirmation, the printer is removed from the registered-printer section. If it is
currently discovered, it remains visible in **Other available printers**. If the user
selects it again and a later document submission starts, it is registered again.

## 6. Business rules

1. IMP-04 is entered after a valid file handoff from IMP-01.
2. Only one printer may be selected at a time.
3. Only an active printer may be selected or handed off.
4. The last used registered printer is selected automatically when it is discovered and
   active.
5. If several registered printers are active, only the most recently used one is
   selected automatically.
6. A manual selection takes precedence over automatic selection for the current screen.
7. Registered printers are ordered by descending most-recent-use time.
8. Other available printers are ordered alphabetically.
9. A printer becomes registered when document submission starts, not merely when it is
   selected on the screen.
10. Registration is retained even if the submission that triggered it fails afterward.
11. There is no limit on the number of registered printers.
12. Forgetting a printer removes only its registered preference; it does not remove a
   currently discovered printer from the discovery results.
13. A registered inactive printer remains visible but is not selectable.
14. Availability is checked again when **Continue** is pressed; the discovery state is
   not treated as final.
15. If no active printer is selected, **Continue** is disabled.
16. No notification is sent outside the IMP-04 screen for availability or selection
   changes.
17. Printer preferences are local to the device and are not uploaded or synchronized.
18. No Internet, cloud service, account, or remote server is required.

## 7. Nominal, alternative, and error cases

### Nominal cases

- The most recently used registered printer is discovered and active: it is automatically
  selected and the user presses **Continue**.
- No registered printer is available: the user selects an active printer from the other
  printers section and continues.
- Multiple printers are available: all are shown, with registered printers first and
  remaining printers alphabetically ordered.
- The user selects another active printer: that printer becomes the sole current
  selection and remains selected while discovery continues.

### Alternative cases

- The remembered printer is not discovered or is inactive: no automatic selection is
  made; the available list is presented, while the registered inactive preference remains
  visible when applicable.
- The remembered printer appears after the screen opens: it is automatically selected if
  the user has not made a manual selection.
- A registered printer is forgotten: it leaves the registered section but remains in the
  other section if currently discovered.
- A forgotten printer is selected and a subsequent submission starts: it is registered
  again.

### Error cases

- The selected printer fails the availability recheck: the user remains on IMP-04, sees
  a localized unavailability message, and can choose another printer.
- No active printer is available: **Continue** is disabled. The empty or discovery
  failure presentation is supplied by IMP-03.
- The selected file is no longer valid: the file-flow feature owns this error; IMP-04
  does not redefine file validation.
- Preference storage is unavailable: the current selection and handoff may continue;
  the product must not present an untrue registered state. The precise user-facing
  treatment of storage failure remains open.

## 8. Acceptance criteria

### List and identification

- **Given** IMP-01 has handed off a valid file, **When** IMP-04 opens, **Then** available
  discovery results are displayed immediately.
- **Given** a discovered printer has identifying data, **Then** its available name,
  model, network address, and state are displayed.
- **Given** registered and other available printers exist, **Then** the registered section
  appears above the other-printers section.
- **Given** multiple registered printers exist, **Then** they are ordered from most
  recently used to least recently used.
- **Given** multiple other available printers exist, **Then** they are ordered
  alphabetically.

### Automatic and manual selection

- **Given** the most recently used registered printer is discovered and active, **When**
  it appears, **Then** it is selected automatically.
- **Given** the remembered printer is not available, **When** the screen is displayed,
  **Then** no unavailable printer is automatically selected and the available list is
  offered.
- **Given** the user manually selects an active printer, **When** additional discovery
  results arrive, **Then** the manual selection remains unchanged.
- **Given** an inactive printer is displayed, **When** the user attempts to select it,
  **Then** selection is refused.
- **Given** no active printer is selected, **Then** **Continue** is disabled.

### Handoff and revalidation

- **Given** an active printer is selected, **When** the user presses **Continue**, **Then**
  availability is checked again.
- **Given** the recheck succeeds, **When** the user presses **Continue**, **Then** the
  selected printer and selected file are handed to the next print-flow step.
- **Given** the recheck fails, **When** the user presses **Continue**, **Then** the user
  remains on IMP-04, sees a localized message, and can choose another printer.

### Registration and forgetting

- **Given** a printer is selected, **When** document submission starts, **Then** that
  printer is registered persistently, even if submission later fails.
- **Given** multiple registered printers exist, **When** the application is reopened,
  **Then** the registered printers are restored locally.
- **Given** a registered printer is forgotten, **When** the user confirms **Forget**,
  **Then** it is removed from the registered section.
- **Given** a forgotten printer is still discovered, **Then** it remains visible in the
  other-printers section.
- **Given** a forgotten printer is selected and a later submission starts, **Then** it
  is registered again.
- **Given** the user starts forgetting a printer, **When** the user cancels the
  confirmation, **Then** the registration remains unchanged.

### Localization and privacy

- **Given** the device uses English, **Then** IMP-04 displays English strings by default.
- **Given** the device uses French, **Then** IMP-04 displays French translations.
- **Given** IMP-04 persists printer preferences, **Then** no printer preference or
  document data is sent outside the device or local network.

## 9. Data and states

### Discovered-printer input

IMP-04 consumes, when available:

- discovery identifier;
- advertised name;
- model;
- network address;
- availability state (`active` or `inactive`);
- discovery origin and updates supplied by IMP-03.

### Registered-printer preference

For each registered printer, IMP-04 retains only the information needed to identify and
reuse the printer locally:

- stable printer identifier;
- identifying name, model, and network address when available;
- most-recent-use timestamp or equivalent ordering information;
- registered preference state.

The preference contains no document content, print history, or print-job status.

### Selection states

- `no selection`: no active printer is selected; **Continue** is disabled.
- `automatic selection`: the latest registered active printer has been selected by the
  application.
- `manual selection`: the user selected an active printer; discovery cannot replace it.
- `revalidation`: availability is being checked before handoff.
- `handoff ready`: revalidation succeeded and the selected printer can be passed onward.
- `selection unavailable`: revalidation failed; the user must choose another printer.

### Registered-printer states

- `registered active`: shown prominently and selectable.
- `registered inactive`: shown compactly and not selectable.
- `not registered`: shown in the other-printers section when currently discovered.

## 10. Out of scope

- Printer discovery, refresh timing, network permissions, and discovery error taxonomy;
  see IMP-03.
- Wi-Fi Direct printer discovery and connection; see IMP-06.
- File selection, file validation, and file access errors; see IMP-01.
- Print configuration and advanced settings.
- Document submission implementation and printer-reported job status.
- Printer testing, printer editing, manual IP entry, and remote printing.
- Persistent print history, queue management, accounts, or synchronization across
  devices.
- System-level notifications.

## 11. Product integration

- **IMP-01 — file selection: dependency.** IMP-04 opens only after receiving a valid
  selected-file handoff. IMP-04 receives the file reference and display metadata but does
  not validate or modify the file.
- **IMP-03 — local printer discovery: dependency.** IMP-04 consumes progressive printer
  results and their observable availability. IMP-03 remains responsible for discovery,
  deduplication, and discovery-specific empty states. IMP-04 performs or requests the
  final availability recheck before handoff.
- **Print configuration/submission: dependency.** IMP-04 hands off the selected printer
  and file. The submission boundary signals the start of sending, which triggers printer
  registration. Configuration and submission behavior are not defined here.
- **IMP-06 — Wi-Fi Direct: out of scope.** Wi-Fi Direct printers must not be implicitly
  mixed into the local Wi-Fi behavior defined here; any integration is specified by
  IMP-06.

## 12. Dependencies, handoffs, and functional impacts

### Input handoff from IMP-01

IMP-04 requires an active standalone print flow containing one valid file reference and
the metadata needed by the following screen. No printer availability is required to
complete IMP-01.

### Input from IMP-03

IMP-04 requires a collection of discovered-printer records that can be matched against
locally registered preferences. A stable identifier is required where available; the
matching contract must also support the identifying data exposed by IMP-03 when a stable
identifier is unavailable.

### Output handoff

After successful availability revalidation, IMP-04 outputs the selected printer and the
active file-flow reference to the next print-flow step. It does not output configuration,
submission status, or job history.

### Functional impacts

- IMP-03 must expose sufficient identity data for registered printers to be recognized.
- The submission feature must expose a clear “submission started” event or equivalent
  handoff to trigger registration.
- Printer preferences must survive application restarts without persisting document
  content or an active print flow.
- English and French resource strings are required; English is the fallback/default.
- No cloud, Internet, account, or system-notification behavior may be introduced by this
  feature.

## 13. Open questions and decisions to confirm

### Confirmed decisions

- IMP-04 is entered after file selection.
- Multiple registered printers are supported without a product-defined limit.
- The latest registered active printer is selected automatically.
- Registered printers are ordered by recency; other printers alphabetically.
- Registration starts when document submission starts, including failed submissions.
- Forgetting requires confirmation and removes only the registration.
- English is the default language and French is supported.

### Open questions

- Define the exact matching fallback when a discovered printer has no stable identifier.
- Define the final English and French wording for the unavailability and forget-confirmation
  messages.
- Define the user-facing treatment if local preference storage fails.
