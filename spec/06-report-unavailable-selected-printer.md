# IMP-09 — Report an Unavailable Selected Printer

## 1. Context and objective

After selecting a printer, the printer may become unusable before submission starts.
IMP-09 prevents sending to that printer and guides the user toward another selection.

The MVP objective is to never use a printer known to be unavailable before submission.
The feature remains local and does not trigger system notifications.

## 2. Scope

### Included

- Checking availability when the user chooses **Print**.
- Using availability updates provided by IMP-03 to immediately disable **Print**.
- Blocking submission when availability is unknown or negative at click time.
- Showing a blocking dialog stating that the printer is no longer available.
- Showing the printer name when available.
- Providing **Choose another printer**, returning to IMP-04.
- Clearing the current selection when the printer becomes unavailable.

### Dependencies

- IMP-03 provides the observable availability state and its updates.
- IMP-04 rechecks availability on **Continue**, keeps the unavailable printer visible and
  non-selectable, and receives the return from this flow.
- IMP-07 defines the start of submission and takes over from that boundary.
- The application's global translation mechanism provides English and French strings.

### Out of scope

- Discovering or rediscovering printers.
- A new periodic check independent from IMP-03.
- Retrying with the same printer.
- Selecting or managing printers, which belong to IMP-04.
- Advanced configuration, effective submission, and job tracking.
- Problems reported after job acceptance, which belong to IMP-11.
- System notifications, cloud, Internet, accounts, and remote printing.
- Wi-Fi Direct.

## 3. Personas and roles

- **Home user:** selects a printer and attempts to print.
- **IMP-03:** provides availability updates.
- **IMP-04:** owns printer selection and allows selecting another printer.
- **IMP-07:** owns submission from the moment it starts.
- **IMP-11:** owns problems reported after job acceptance.

## 4. User stories

- As a user, I want to be prevented from using an unavailable printer so that I do not
  waste time or attempt a submission that cannot succeed.
- As a user, I want to understand why printing is blocked so that I can choose another
  printer.

## 5. Functional journeys

### Nominal journey

1. The user reaches the print screen with a valid file and a selected printer.
2. The printer is reported as available.
3. **Print** is enabled.
4. The user chooses **Print**.
5. IMP-09 checks that availability is positive.
6. The positive check allows IMP-07 to start submission.

### Printer becomes unavailable before the click

1. IMP-03 reports that the printer is no longer available.
2. The current selection is cleared.
3. **Print** is disabled.
4. The user returns to IMP-04 to choose another printer.

### Unavailability detected at click time

1. The user chooses **Print** while the state is negative or unknown.
2. Submission is blocked.
3. A blocking dialog shows a simple message and the printer name when available.
4. The user chooses **Choose another printer**.
5. Impresso returns to IMP-04.

## 6. Business rules

1. A printer known to be unavailable cannot be used.
2. Unknown availability is treated as not authorized for submission.
3. Availability updates from IMP-03 may disable **Print** before any user action.
4. A check is performed when the user chooses **Print**.
5. If that check fails, IMP-07 must not start submission.
6. The dialog blocks the underlying screen and offers **Choose another printer**.
7. Unavailability clears the current selection.
8. The unavailable printer remains visible in IMP-04 but cannot be selected.
9. Returning to IMP-04 does not automatically start a new discovery.
10. IMP-09 applies until submission starts. After that boundary, IMP-07 and IMP-11 apply
    according to their own rules.
11. No system notification is sent.
12. No document or printer state is transmitted outside the local network.

## 7. Nominal, alternative, and error cases

### Nominal cases

- The printer is available at click time: submission is handed off to IMP-07.
- The printer becomes unavailable before the click: the action is disabled and the
  selection is cleared.

### Alternative cases

- The printer name is unavailable: the message is shown without a name.
- The user chooses another printer in IMP-04: the journey resumes according to that
  feature's rules.
- Another selected printer becomes unavailable: the same behavior applies.

### Error cases

- Negative availability at click time: submission is refused and the dialog is shown.
- Unknown availability at click time: submission is refused and the same dialog is shown.
- A problem after submission starts: out of scope for IMP-09; see IMP-07 and IMP-11.

## 8. Acceptance criteria

- **Given** a printer is reported unavailable before the user chooses **Print**, **When**
  the state is received, **Then** **Print** is disabled and the selection is cleared.
- **Given** a printer is unavailable, **When** the user views IMP-04, **Then** it remains
  visible, is marked unavailable, and cannot be selected.
- **Given** the printer availability is unknown, **When** the user chooses **Print**,
  **Then** submission is blocked.
- **Given** availability is negative or unknown, **When** submission is blocked, **Then**
  a blocking dialog is displayed.
- **Given** the dialog is displayed, **When** the user chooses **Choose another printer**,
  **Then** Impresso returns to IMP-04.
- **Given** the printer name is available, **When** the dialog is displayed, **Then** the
  name appears in the message.
- **Given** the printer is available, **When** the user chooses **Print**, **Then** IMP-09
  authorizes the handoff to IMP-07.
- **Given** submission has started, **When** a problem is reported afterward, **Then**
  IMP-09 does not redefine the behavior and IMP-11 takes over.
- **Given** unavailability is detected, **Then** no system notification, account, or
  remote service is required.

## 9. Data and states

### Consumed data

- Identifier of the selected printer.
- Printer name, when available.
- Availability state provided by IMP-03.
- Result of the check performed when the user chooses **Print**.

### Produced data

- Current selection cleared when the printer is unavailable.
- Handoff authorized to IMP-07 or submission refused.
- Request to return to IMP-04.

### States

- `available`: printing may be attempted.
- `unavailable`: printing is blocked and the selection is cleared.
- `unknown`: printing is blocked as a precaution.
- `checking`: availability is being checked after **Print** is chosen.
- `redirect-to-selection`: the dialog has been handled and a return to IMP-04 is requested.
- `submission-started`: IMP-07 has taken over; IMP-09 is complete for this journey.

## 10. Out of scope

Printer discovery, selection, persistence, and printer availability management remain
defined by IMP-03 and IMP-04. Transmission, retry, job tracking, and post-acceptance
problems remain defined by IMP-07, IMP-10, and IMP-11. Advanced settings, Wi-Fi Direct,
and system notifications are not added by IMP-09.

## 11. Product integration

- **IMP-03 — dependency:** exposes the availability updates used to disable the action.
- **IMP-04 — dependency:** provides the initial selection, receives the return, and keeps
  the unavailable printer visible but non-selectable.
- **IMP-07 — dependency:** receives only a printer for which IMP-09 authorized submission.
- **IMP-11 — out of scope:** handles problems after job acceptance.
- **Global localization — dependency:** provides translations for the dialog and action.

## 12. Dependencies, handoffs, and functional impacts

### Incoming handoff

IMP-09 receives a selected printer from IMP-04 and the current availability state from
IMP-03.

### Outgoing handoff

- If the check succeeds, IMP-09 authorizes the handoff to IMP-07.
- Otherwise, IMP-09 clears the selection, displays the dialog, and requests a return to
  IMP-04.

### Functional impacts

- IMP-03 must expose availability changes to the print screen.
- IMP-04 must accept the return without automatically restarting discovery.
- IMP-07 must distinguish a pre-submission refusal from a failure after submission starts.
- Text must follow the application's global localization mechanism.

## 13. Open questions and decisions to confirm

### Confirmed decisions

- The objective is to prevent use of an unusable printer.
- The check occurs when **Print** is chosen, while IMP-04 checks are retained.
- Unknown availability blocks submission.
- The dialog is blocking and returns to IMP-04.
- The current selection is cleared.
- No retry with the same printer or system notification is planned for the MVP.

### Open questions

No blocking functional question remains.
