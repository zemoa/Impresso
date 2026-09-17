# IMP-14 — Keep Documents on the Local Network

## 1. Context and objective

Impresso is intended to provide local-first printing. A user must be able to print a
document without the document, print data, or data produced by Impresso being sent to a
cloud service, an Internet server, or another remote service.

The objective is to make locality a non-negotiable product constraint across all Impresso
features. This constraint is preventive and silent: the product does not offer a remote
fallback, a warning-based bypass, or a user setting to authorize external transmission.

## 2. Scope

### Included

- Preventing external transmission of document content.
- Preventing external transmission of print data required to submit a job.
- Preventing external transmission of metadata produced by Impresso.
- Keeping technical logs produced by Impresso on the device.
- Applying the rule to all current and future Impresso functionality that handles these
  data categories.
- Technical non-regression validation of the absence of external calls or transmissions.

### Dependencies

- Local printer communication must remain possible through the local network or another
  product-approved local connection type.
- Each feature that handles a document, print data, metadata, or Impresso log must comply
  with this specification.

### Out of scope

- Services or applications selected through the Android file picker; their behavior is
  outside Impresso's control.
- Android-managed cloud backup or device-transfer behavior.
- Detailed validation that a printer address is local; printer discovery owns that concern.
- Defining printer discovery, printer selection, submission, or job-status behavior.
- Displaying privacy information or warnings to the user.
- Providing a user-controlled exception or external-printing mode.

## 3. Personas and roles

- **Home user:** expects the selected document to remain within the local printing
  environment without needing to configure privacy options.
- **Impresso features:** must not emit the covered data to an external destination.
- **Local printer:** receives the document and print data through an approved local
  connection.
- **Android platform:** provides file access and may provide system-managed services that
  are outside this feature's control.

## 4. User stories

- As a user, I want my document to remain on my local network so that it is not sent to a
  cloud or remote server.
- As a user, I want print data and Impresso metadata to remain local so that printing does
  not disclose information externally.
- As a user, I want this protection to work silently without having to configure a
  privacy option.

## 5. Functional journeys

### 5.1 Selecting a document

1. The user selects a supported document through the Android file picker.
2. Impresso retains only the reference and metadata needed by the active flow.
3. Impresso does not create or send a remote copy of the document.

The file picker's provider and any service it uses are outside this feature's scope.

### 5.2 Discovering and selecting a printer

1. Impresso discovers and verifies printers using the product's approved local connection
   mechanisms.
2. Discovery and selection do not send document content, print data, or Impresso logs to
   an external destination.
3. Printer-address locality rules remain owned by the discovery feature.

### 5.3 Submitting a print job

1. The user submits the selected document to the selected printer.
2. The document and data required for printing are sent only through the approved local
   printer connection.
3. No remote or cloud submission is attempted before, during, or after the local
   submission.
4. The user sees the normal printing flow; no privacy warning or confirmation is added.

### 5.4 Logging

Impresso may produce technical logs needed for local operation or diagnosis, but those
logs remain on the device. They must not contain or transmit a remote copy of the
document or print payload.

## 6. Business rules

1. Impresso must not transmit document content outside the local printing environment.
2. Impresso must not transmit print data outside the local printing environment.
3. Impresso must not transmit metadata produced by the application to a remote service.
4. Technical logs produced by Impresso remain on the device.
5. Impresso must not use an Internet, cloud, or remote-service fallback when local
   printing is unavailable.
6. The locality rule applies to every Impresso feature handling the covered data.
7. The rule is silent and cannot be bypassed by the user.
8. Local-network communication with the printer is permitted; the presence of Android's
   network capability or permission does not authorize remote transmission.
9. Android file-picker providers and Android-managed backups are not governed by this
   feature.
10. No account, remote server, or cloud service is required for compliance.

## 7. Nominal, alternative, and error cases

### Nominal cases

- A supported document is selected and remains represented by a local file reference.
- A printer is discovered on an approved local connection.
- A print job sends the document and required print data to that local printer only.
- Technical diagnostics, when produced, remain local to the device.

### Alternative cases

- The user selects a file supplied by an Android provider: provider behavior remains out
  of scope, while Impresso does not create an additional remote copy.
- The printer is reached through a future product-approved local connection type: the same
  locality rule applies.
- Local printing is unavailable: Impresso reports the owning feature's local error and
  does not attempt a remote fallback.

### Error cases

- An external service would be required to complete printing: the operation is not
  authorized by Impresso and no bypass is offered.
- A feature attempts to send covered data to a remote destination: this is a product
  non-compliance defect, not a user-confirmable error path.
- A technical log would contain or export covered data: the log behavior is non-compliant
  and must be corrected; no user-facing opt-in is introduced.

## 8. Acceptance criteria

- **Given** a user selects a supported document, **When** Impresso retains it for the
  active flow, **Then** only the local reference and required metadata are retained by
  Impresso.
- **Given** a document is selected, **Then** Impresso does not create or transmit a copy
  to a cloud service, Internet server, or other remote service.
- **Given** printer discovery runs, **Then** it uses only approved local connection
  mechanisms for the product scope.
- **Given** a print job is submitted, **When** the document and print data are sent,
  **Then** they are sent only to the selected local printer connection.
- **Given** Impresso produces metadata for its flow, **Then** that metadata is not sent to
  a remote service.
- **Given** Impresso produces technical logs, **Then** those logs remain on the device and
  are not exported by Impresso.
- **Given** local printing is unavailable, **When** the operation cannot proceed locally,
  **Then** Impresso does not fall back to Internet, cloud, or remote printing.
- **Given** an external transmission would be needed, **When** the operation is evaluated,
  **Then** Impresso does not authorize or offer a bypass for that transmission.
- **Given** the user follows any current Impresso flow, **Then** no privacy warning,
  external-destination choice, or privacy configuration is required.
- **Given** a release is validated, **When** network behavior is tested, **Then** tests
  verify that no covered data is sent to an external destination.

## 9. Data and states

### Covered data

- Document content and the document reference used by Impresso.
- Print data required to submit or configure a job.
- Metadata produced by Impresso about the document, printer, or print flow.
- Technical logs produced by Impresso.

### Allowed destination

- The selected printer through an approved local connection.
- The device itself for transient flow state and local technical logs.

### Prohibited destinations

- Internet services.
- Cloud services and remote servers.
- Any other destination outside the approved local printing environment.

### Feature state

- `local-only`: the operation uses only an allowed local destination.
- `blocked-by-locality`: an operation would require a prohibited destination and is not
  authorized.

The feature does not require a user-visible state transition. Existing feature screens
retain ownership of their loading, success, and error states.

## 10. Out of scope

- Behavior of Android file providers, cloud-storage applications, or Android-managed
  backup services.
- Printer address classification and local-network discovery policy.
- Printer protocol details and connection implementation.
- Print configuration and job-status terminology.
- Privacy notices, consent screens, telemetry controls, or user settings.
- Remote printing, cloud printing, accounts, synchronization, and server-side queues.

## 11. Integration with the product

- **IMP-01 — dependency:** supplies a local document reference and required display
  metadata; it must not cause Impresso to create a remote copy.
- **IMP-03 — dependency:** discovers printers through approved local mechanisms; its
  discovery-specific behavior remains defined by IMP-03.
- **IMP-04 — dependency:** hands off a locally discovered and selected printer; selection
  behavior remains defined by IMP-04.
- **IMP-07 — dependency:** sends document and print data to the selected local printer
  only; submission behavior remains defined by IMP-07.
- **IMP-09 — dependency:** prevents submission to an unavailable printer; it does not
  authorize a remote fallback.
- **IMP-10/IMP-11 — dependency:** handle post-submission status and printer problems
  without transmitting covered data to a remote service.
- **Android file picker and backup — out of scope:** Impresso does not define provider or
  system-managed backup behavior.

## 12. Dependencies, handoffs, and functional impacts

### Incoming handoffs

- IMP-01 hands off a document reference and the metadata required by the active flow.
- IMP-03/IMP-04 hand off the selected printer and its approved local connection data.

### Outgoing handoffs

- IMP-07 receives the document and selected printer for local submission.
- IMP-10/IMP-11 receive only the information needed for local status and printer-problem
  handling.

### Functional impacts

- No current Impresso flow may introduce an external data destination for covered data.
- Failure of local printing must not activate a remote fallback.
- User-visible behavior remains unchanged: the constraint is enforced silently.
- Validation must include network inspection or equivalent tests for external calls and
  transmissions.
- The Android `INTERNET` capability may support local printer communication, but must not
  be used as a product authorization for Internet or cloud communication.

## 13. Questions open and decisions to confirm

### Confirmed decisions

- IMP-14 applies to all Impresso functionality.
- Documents, print data, Impresso metadata, and Impresso technical logs are covered.
- Logs produced by Impresso remain on the device.
- Android file-picker providers and Android-managed backups are out of scope.
- The rule is preventive, silent, and has no bypass or warning flow.
- No Internet, cloud, or remote fallback is permitted.
- Validation is primarily technical, through non-regression tests.

### Open questions

No blocking product question remains for the validated scope.
