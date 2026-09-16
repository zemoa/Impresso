# IMP-01 - Select a File to Print

## Context

The user needs to tell Impresso which document or image they want to print. This feature is the entry point of the standalone printing flow.

## Goal

Allow an Android user to select one supported file, verify that it is accessible, see what was selected, and continue to the next step of the printing flow.

This feature ends after a valid file has been selected. Printer discovery and printing are separate features.

## Scope

### In scope

- Display an action to choose a file.
- Open the Android system file picker.
- Accept one file in one of these formats:
  - PDF;
  - JPEG or JPG;
  - PNG;
  - GIF;
  - WebP;
  - DOCX;
  - ODT.
- Validate the declared file type and the accessibility of the returned file.
- Display the selected file name and type before the user continues.
- Allow the user to replace the selected file.
- Allow the user to continue to the next step.
- Handle cancellation, unsupported files, and inaccessible files.

### Out of scope

- Discovering or selecting a printer.
- Configuring print settings.
- Converting or modifying document content.
- Sending a print job to a printer.
- Selecting or printing multiple files.
- Supporting ODS or ODP files.
- Handling document passwords or encryption.
- Persistent storage, print history, or a print queue.
- Android Share/Open With entry points.
- Broad storage permissions.

## User-facing behavior

### Initial state

The screen displays:

- a title explaining that the user must select a file to print;
- a primary action labelled **Choose a file**;
- no available **Continue** action until a valid file has been selected.

### Selecting a file

1. The user taps **Choose a file**.
2. Impresso opens the Android system file picker.
3. The picker is requested to show supported file types.
4. The user selects one file.
5. Impresso validates the returned type and checks that the file can be accessed.
6. While validation is running, the UI displays a loading state and prevents continuation or duplicate selection actions.
7. If validation succeeds, the selected file is displayed and **Continue** becomes available.

If a file provider returns a file that does not match the supported types despite the picker filter, Impresso rejects it and displays an actionable error.

### Selected-file state

The screen displays at least:

- the selected file name;
- a human-readable file type;
- the **Choose another file** action;
- the **Continue** action.

The selected file remains available only for the current print flow. It is not restored after the flow is left and the app is reopened.

### Replacing the file

When the user taps **Choose another file**, the current file remains the active selection until the new file has passed validation.

- A valid new file replaces the previous file.
- Cancellation keeps the previous file selected.
- An unsupported or inaccessible new file does not replace the previous file.

### Continuing

When the user taps **Continue**, Impresso checks that the selected file is still accessible before handing it to the next feature. If it is no longer accessible, Impresso keeps the user on the selection screen and displays an actionable error.

The next feature receives a reference to the selected file and the metadata required to display it, including its name and type.

### Back navigation

Pressing Back leaves the current standalone print flow. The current selection is not persisted for a later session.

## Functional rules

- Only one file can be selected in a print flow.
- A file must be selected and valid before the user can continue.
- The supported initial formats are PDF, JPEG/JPG, PNG, GIF, WebP, DOCX, and ODT.
- There is no product-defined maximum file size in this version.
- File type and accessibility are validated; full document-structure validation is not part of this feature.
- A password-protected or encrypted file is not specifically rejected at selection time; handling it belongs to a later printing step.
- The displayed name must match the selected file.
- The selected file is not uploaded to the Internet or any remote service.
- No account, server, or Internet connection is required.
- The feature must not require broad storage access; it uses the Android system picker and its granted file reference.
- Printer availability must not prevent file selection.

## Acceptance criteria

- [ ] From the initial state, the user can open the Android system file picker by tapping **Choose a file**.
- [ ] The picker is requested to display the supported file types.
- [ ] The user can select a PDF file.
- [ ] The user can select a JPEG/JPG, PNG, GIF, or WebP image.
- [ ] The user can select a DOCX file.
- [ ] The user can select an ODT file.
- [ ] After successful validation, the selected file name and type are visible before continuation.
- [ ] **Continue** is unavailable before a valid file is selected and available after successful validation.
- [ ] A loading state prevents continuation and duplicate actions during validation.
- [ ] The user can replace the selected file with another supported file.
- [ ] Cancelling a replacement keeps the previously selected file.
- [ ] Rejecting a replacement keeps the previously selected file.
- [ ] An unsupported file returned by a provider is rejected with an actionable message.
- [ ] An inaccessible file is rejected with an actionable message.
- [ ] If the selected file becomes inaccessible before continuation, the user cannot continue and is told to choose another file.
- [ ] Cancelling the picker returns to the selection screen without an error.
- [ ] Pressing Back leaves the standalone print flow.
- [ ] No account, server, or Internet connection is requested during file selection.
- [ ] No file content is transmitted to a remote service during this feature.

## Error and empty states

### No file selected

The initial state remains visible. **Continue** is unavailable, and the screen explains that a file must be selected before proceeding.

### Picker cancelled

Impresso returns to the selection screen without showing an error. If a file was already selected, it remains selected.

### Unsupported file

Impresso displays an actionable message such as: **This file type is not supported. Choose a PDF, image, DOCX, or ODT file.** The file is not accepted as the active selection.

### Inaccessible or unreadable reference

Impresso displays an actionable message such as: **This file is no longer available. Choose another file.** The file is not accepted as the active selection.

### File picker unavailable

If Android cannot open a file picker, Impresso displays a clear error and does not simulate a file selection.

## Data and privacy

- The feature uses the Android system file picker rather than broad storage access.
- Impresso retains only the file reference and metadata required for the current flow.
- No remote copy of the file is created.
- No file content is sent to an external service.
- The selection is not stored in print history or a persistent queue.

## Dependencies and impacts

- The selected file must be handed off to the printer-discovery and printer-selection flow (`IMP-03` / `IMP-04`).
- The handoff must provide an accessible file reference plus the file name and type needed by the next screen.
- Functional validation must cover PDF, each supported image family, DOCX, ODT, an unsupported file, cancellation, and a file that becomes inaccessible before continuation.

## Open questions

- [ ] Confirm the final English copy with UX/content review.
- [ ] Confirm whether the Android picker can consistently filter all declared formats across supported Android versions and file providers.
