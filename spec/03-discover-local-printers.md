# IMP-03 — Discover Local Printers

## 1. Context and goal

Users should be able to print without manually entering printer information. Impresso
automatically discovers available printers to reduce setup time.

Discovery starts when the application opens and runs in the background. It provides
IMP-04, the printer-selection feature, with an identifiable list of printers and their
availability state.

The MVP targets the Epson XP-6000 Series, while the discovery contract must remain
extensible to other brands and protocols.

## 2. Scope

### Included

- Automatic discovery when the application starts.
- Discovery on the local Wi-Fi network.
- Background execution that does not block file selection.
- Progressive display of results as printers are discovered.
- Display of every discovered printer, without duplicates.
- Identification using the available combination of name, model, and network address.
- An `active` or `inactive` state for each printer.
- Manual discovery retry.
- In-memory retention of results for the whole application session.
- Maximum discovery duration of 30 seconds.
- MVP discovery implementation for the Epson XP-6000 Series.

### Dependencies

- IMP-01 provides the selected file, but file selection must not be blocked by
  discovery.
- IMP-04 displays the list, selects a printer, and manages persistence of the last
  selected printer.
- IMP-04 rechecks printer availability when the user selects a printer.
- IMP-06 handles Wi-Fi Direct discovery separately.

### Out of scope

- Persisting or forgetting the last selected printer.
- Wi-Fi Direct discovery.
- Manual printer entry.
- Sending, configuring, or tracking a print job.
- Implementing brands other than Epson in the MVP.
- Print history, persistent queues, or durable storage of discovery results.
- Detailed categorization of technical errors other than the absence of Wi-Fi.

## 3. Personas and roles

- **Home user:** opens Impresso, may select a file, and views available local printers.
- **IMP-04:** functional consumer of discovery results; IMP-03 does not decide how the
  selected printer is persisted.

## 4. User stories

- As a user, I want local printers to be discovered automatically so that setup takes
  less time.
- As a user, I want printers to appear as soon as they are discovered so that I can
  continue quickly.
- As a user, I want to retry discovery so that a printer that has become available can
  be found.
- As a user, I want to distinguish printers by their visible information and state so
  that I know which one can be used.

## 5. Functional flows

### Initial discovery

1. The user opens the application.
2. Impresso checks whether the device is connected to Wi-Fi.
3. If Wi-Fi is available, Impresso starts discovery in the background.
4. Each discovered printer is immediately added to the list if it is not already
   present.
5. Discovery ends when no more results are received or after 30 seconds at the latest.
6. Results remain available in memory for the session.

Initial discovery must not block file selection or navigation.

### Viewing results

When the user opens the printer screen, results already available are displayed
immediately. Returning to the foreground does not automatically start a new discovery.

### Manual retry

1. The user requests a new discovery.
2. Impresso clears the displayed results.
3. Impresso checks the Wi-Fi connection.
4. If Wi-Fi is available, a new discovery starts.
5. New results are displayed progressively.

### Leaving the screen

The user may leave the printer screen and continue selecting a file while discovery is
running. Results already discovered remain available when the user returns.

## 6. Business rules

1. IMP-03 covers the local Wi-Fi network; Wi-Fi Direct belongs to IMP-06.
2. Discovery starts when the application opens.
3. If there is no Wi-Fi connection, discovery fails immediately.
4. A discovery run cannot last longer than 30 seconds.
5. Results are displayed as they are discovered.
6. The same printer must appear only once in a result list.
7. All discovered printers are displayed; IMP-03 applies no preferred-printer rule.
8. A printer is `active` when it responds to discovery or the relevant verification;
   it is `inactive` when it no longer responds.
9. An `inactive` printer remains visible but cannot be selected in IMP-04.
10. Availability must be checked again when IMP-04 attempts selection.
11. Results are retained only in memory during the application session.
12. No document, discovery result, or discovery data may be sent to the Internet, a
    cloud service, or a remote server.

## 7. Nominal, alternative, and error cases

### Nominal cases

- One or more Epson XP-6000 Series printers respond: they appear progressively with
  their available information and the `active` state.
- Multiple printers respond: all are displayed without duplicates.
- The user opens the printer screen after discovery has started: results already found
  are visible immediately.

### Alternative cases

- No printer responds within 30 seconds: an empty state is displayed with **Retry
  discovery**.
- A previously discovered printer stops responding: it remains visible as `inactive`
  and cannot be selected in IMP-04.
- The user retries discovery: previous results are removed and a new list is built
  progressively.
- The application returns to the foreground: in-memory results are retained and no
  automatic discovery starts.

### Errors

- The device is not connected to Wi-Fi: discovery fails immediately and the user is
  told to restore Wi-Fi and retry.
- Another technical discovery error occurs: the MVP does not expose an additional
  functional error category; the observable result is no result and the ability to
  retry discovery.

## 8. Acceptance criteria

### Startup and execution

- **Given** the application is open and the device is connected to Wi-Fi, **When** the
  session starts, **Then** printer discovery starts automatically.
- **Given** discovery is running, **When** the user selects a file, **Then** file
  selection remains usable.
- **Given** discovery is running, **When** the user leaves the printer screen, **Then**
  discovery may continue in the background.

### Results

- **Given** a printer responds to discovery, **When** it is detected, **Then** it
  appears without waiting for discovery to finish.
- **Given** multiple printers respond, **When** they are detected, **Then** all are
  displayed.
- **Given** the same printer is detected more than once, **When** results are received,
  **Then** it appears only once.
- **Given** a printer is displayed, **Then** the list shows available identifying
  information and its `active` or `inactive` state.
- **Given** the user opens the printer screen after discovery has started, **Then**
  results already found are displayed immediately.

### Retry and duration

- **Given** a discovery has produced results, **When** the user retries, **Then** old
  results are cleared before new results are displayed.
- **Given** no printer is found, **When** 30 seconds have elapsed, **Then** an empty
  state with **Retry discovery** is displayed.
- **Given** the application returns to the foreground, **When** results are in memory,
  **Then** they are retained and no automatic discovery starts.

### Wi-Fi and availability

- **Given** the device is not connected to Wi-Fi, **When** discovery should start,
  **Then** it fails immediately with appropriate information and a retry option.
- **Given** a printer no longer responds, **When** its state is checked, **Then** it
  remains visible as `inactive` and cannot be selected in IMP-04.
- **Given** a printer is displayed as active, **When** IMP-04 requests selection,
  **Then** availability is checked again before selection is allowed.

### Locality and privacy

- **Given** discovery is running, **Then** no Internet or cloud discovery is performed.
- **Given** discovery is running, **Then** no document or result is transmitted outside
  the local network.

## 9. Data and states

### Discovered printer data

- Advertised name, when available.
- Model, when available.
- Network address, when available.
- Identifier used for deduplication during the session.
- Availability state: `active` or `inactive`.
- Discovery origin: local Wi-Fi network.

Missing identifying information must not prevent display when the printer can still be
distinguished using the other available data.

### Discovery states

- `not started`: no discovery is active.
- `running`: discovery accepts and displays results progressively.
- `results available`: at least one printer has been discovered.
- `no results`: discovery ended without finding a printer.
- `wifi failure`: the device was not connected to Wi-Fi when discovery started.

Results and discovery state are transient and are not restored after the application
session ends.

## 10. Out of scope

The following must not be added to this feature:

- printer selection or persistence of that selection;
- forgetting a persisted printer;
- Wi-Fi Direct connectivity;
- remote or cloud discovery;
- adding a printer by IP address;
- print testing;
- copies, paper, orientation, or duplex configuration;
- job tracking or history;
- effective support for a non-Epson brand in the MVP.

## 11. Product integration

- **IMP-01 — file selection: included as a coexistence point.** Discovery starts
  independently and does not block file selection or validation.
- **IMP-04 — printer selection: dependency.** IMP-03 provides identifiable results,
  their state, and observable availability. IMP-04 manages final display, selection,
  revalidation, and persistence.
- **IMP-06 — Wi-Fi Direct: out of scope.** It must provide a separate integration and
  must not be implicitly added to local Wi-Fi discovery.
- **Print flow: out of scope.** IMP-03 neither configures nor submits a print job.

## 12. Dependencies, handoffs, and functional impacts

### Handoff to IMP-04

IMP-03 provides a temporary collection of printers containing, when available, the
name, model, network address, deduplication identifier, and availability state.

IMP-04 must not treat the received state as final: availability is rechecked at
selection time. If the printer no longer responds, it remains visible, selection is
refused, and its state is updated.

### Functional impacts

- The empty state must allow the user to retry discovery.
- Wi-Fi failure must be distinguishable from no printer being discovered.
- Discovery must remain usable without a selected file.
- IMP-03 must not persist results durably.
- No fallback to the Internet or cloud is allowed.

## 13. Open questions and decisions to confirm

No blocking product question remains for the validated scope.

Recorded decisions:

- maximum discovery duration: 30 seconds;
- target network: local Wi-Fi;
- MVP implementation: Epson XP-6000 Series;
- discovery contract: extensible to other brands and protocols;
- persistence of the selected printer: IMP-04;
- Wi-Fi Direct: IMP-06.
