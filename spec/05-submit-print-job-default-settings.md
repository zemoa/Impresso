# IMP-07 — Submit a Print Job with Default Settings

## 1. Context and objective

After selecting a valid document and an available printer, the user must be able to
start printing immediately without configuring advanced options.

The objective of this feature is to send one selected document to one selected printer
using the printer's default settings.

Printing is considered successful when the job has been accepted for transmission to
the printer. The physical completion of printing and subsequent printer-reported status
belong to other features.

## 2. Scope

### Included

- Displaying a **Print** action when a valid document and active printer are available.
- Sending one document to the selected printer.
- Using the printer's default settings.
- Displaying an in-progress state during transmission.
- Preventing duplicate submissions.
- Displaying a success confirmation.
- Returning to the home screen after successful submission.
- Reporting transmission failures and offering **Retry**.
- Continuing a submission that has already started if the user leaves the screen or
  application.

### Dependencies

- **IMP-01** provides the validated document reference and metadata.
- **IMP-04** provides the selected printer after availability revalidation.
- Existing product rules define communication timeout behavior.
- **IMP-10** handles detailed job status after submission.
- **IMP-11** handles problems reported by the printer after job acceptance.

### Out of scope

- Selecting or validating the document.
- Discovering or selecting the printer.
- Advanced print settings.
- Printing multiple documents.
- Cancelling a transmission after it has started.
- Detailed job tracking, print history, or persistent queues.
- System notifications.
- Cloud, Internet, or remote printing.

## 3. Personas and roles

- **Home user:** starts printing and reacts to submission success or failure.
- **IMP-01:** supplies the valid document.
- **IMP-04:** supplies the selected printer.
- **IMP-10:** owns post-submission job-status display.
- **IMP-11:** owns printer-reported problems after job acceptance.

## 4. User stories

- As a user, I want to press **Print** so that my selected document is sent to my
  selected printer.
- As a user, I want printing to use default settings so that I do not need to configure
  anything.
- As a user, I want to know whether submission succeeded or failed.
- As a user, I do not want an accidental double submission.

## 5. Functional journeys

### 5.1 Nominal journey

1. The user has a valid selected document.
2. The user has selected an active printer.
3. The user presses **Print**.
4. Impresso starts transmitting the document with the printer's default settings.
5. The interface displays an in-progress state and disables duplicate actions.
6. The printer accepts the submission.
7. Impresso displays a success confirmation.
8. Impresso returns the user to the home screen.

### 5.2 Leaving during submission

If transmission has started, leaving the screen or application does not cancel the
submission. Transmission continues according to the existing communication and timeout
rules.

No system notification is required.

### 5.3 Retrying a failed transmission

If transmission fails before the printer accepts the job, Impresso remains in the print
flow, displays an actionable error, and offers **Retry**. A retry starts a new attempt
with the same document and printer, subject to current availability.

## 6. Business rules

1. Printing requires one valid document.
2. Printing requires one active selected printer.
3. The user cannot submit without both prerequisites.
4. The submission uses the printer's default settings.
5. Only one submission attempt may be active at a time.
6. Starting transmission disables duplicate submission actions.
7. An already-started submission is not cancelled when the user leaves the screen or
   application.
8. Success means that the document has been accepted for transmission to the printer.
9. A transmission failure is distinct from a printer problem reported after job
   acceptance.
10. Timeout handling follows the existing product rule and is not redefined here.
11. No document data is sent outside the local network.
12. No account, cloud service, or Internet connection is required.

## 7. Nominal, alternative, and error cases

### Nominal cases

- A valid document is successfully sent using the selected printer's default settings.
- The user sees a success confirmation and is returned to the home screen.

### Alternative cases

- The user leaves the screen while transmission is active: transmission continues.
- The user leaves and returns after completion: the latest available submission result is
  displayed in the active flow.
- The user retries after a transmission failure.

### Error cases

- No valid document is available: **Print** is unavailable; file validation remains owned
  by IMP-01.
- No active printer is selected: **Print** is unavailable; printer selection remains
  owned by IMP-04.
- The printer is unavailable before submission: handled by IMP-09.
- Transmission fails before acceptance: IMP-07 displays an error and offers **Retry**.
- The printer reports a problem after accepting the job: handled by IMP-11.
- Job status is unavailable after submission: handled by IMP-10.

## 8. Acceptance criteria

- **Given** a valid document and an active selected printer, **When** the user presses
  **Print**, **Then** Impresso starts sending the document immediately.
- **Given** a valid document and an active selected printer, **When** submission starts,
  **Then** the printer's default settings are used.
- **Given** submission is in progress, **Then** duplicate submission actions are
  disabled.
- **Given** submission is in progress, **When** the user leaves the screen or
  application, **Then** the submission is not cancelled.
- **Given** the printer accepts the document, **When** transmission completes, **Then**
  Impresso displays a success confirmation.
- **Given** submission succeeds, **Then** Impresso returns the user to the home screen.
- **Given** transmission fails before printer acceptance, **Then** Impresso displays an
  actionable error.
- **Given** a transmission failure is displayed, **When** the user presses **Retry**,
  **Then** a new submission attempt starts.
- **Given** no valid document exists, **Then** the print action is unavailable.
- **Given** no active printer is selected, **Then** the print action is unavailable.
- **Given** a printer reports a problem after accepting the job, **Then** that problem is
  handled by IMP-11 rather than IMP-07.
- **Given** submission is performed, **Then** no document data is transmitted outside
  the local network.
- **Given** the user starts printing, **Then** no account, server, cloud service, or
  system notification is required.

## 9. Data and states

### Input data

- Valid document reference.
- Document display name and type.
- Selected printer identity and connection information.
- Printer default settings.

### Output data

- Submission result: accepted or transmission failed.
- Information required by IMP-10 when a job has been accepted, including a job
  identifier when available.

### Submission states

- `ready`: document and printer are available.
- `submitting`: transmission is in progress.
- `submitted`: printer has accepted the job.
- `failed`: transmission failed before acceptance.
- `retrying`: a new submission attempt is being started.

Submission state is transient and is not added to print history or a persistent queue.

## 10. Out of scope

- File selection, file validation, or file conversion.
- Printer discovery, selection, or registration.
- Copies, orientation, color, duplex, paper size, or page-range configuration.
- Cancellation of an active transmission.
- Monitoring the physical printing process.
- Persistent job history or queue management.
- System-level notifications.
- Remote or cloud submission.

## 11. Integration with the product

- **IMP-01 — dependency:** provides the valid document reference and display metadata.
- **IMP-04 — dependency:** provides the selected printer after availability revalidation.
- **IMP-09 — dependency:** handles a printer becoming unavailable before submission.
- **IMP-10 — dependency:** consumes the accepted-submission handoff and displays
  available job status.
- **IMP-11 — dependency:** handles printer-reported problems after job acceptance.
- **Home screen — included:** receives the user after successful submission.
- **Advanced settings — out of scope:** no advanced settings are required or displayed.

## 12. Dependencies, handoffs, and functional impacts

### Handoff into IMP-07

IMP-07 requires one valid document reference, its display metadata, and one selected
active printer with the connection information required for submission.

### Handoff from IMP-07

After acceptance, IMP-07 provides IMP-10 with the submission result and any available
job identifier or status reference. IMP-07 does not define how subsequent status is
obtained or displayed.

### Functional impacts

- The print-flow state must distinguish an active submission from a completed or failed
  attempt.
- The submission must remain active when the user leaves the screen.
- The interface must prevent accidental duplicate submissions.
- Transmission failures must be distinguishable from post-submission printer problems.
- Submission must remain local to the printer connection.

## 13. Open questions and decisions to confirm

No blocking functional questions remain.

Confirmed decisions:

- The user selects a document and then prints it.
- The user selects the printer before printing.
- Printing starts immediately when **Print** is pressed.
- Printer default settings are used.
- An in-progress state is displayed.
- Successful submission displays a confirmation and returns to the home screen.
- A started submission continues if the user leaves the screen or application.
- Transmission failure offers **Retry**.
- Timeout behavior is inherited from the existing product rule.
- Detailed status belongs to IMP-10.
- Post-acceptance printer problems belong to IMP-11.
