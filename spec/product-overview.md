# Impresso Product Specification

## Context

Home users often depend on unreliable, restrictive, or poor-quality proprietary printer applications. Impresso provides a simple and dependable alternative for printing from Android without sending user data to the Internet.

## Goal

Provide an open-source Android application that allows a home user to print documents and images securely and reliably to a printer available on the local network.

## Scope

- In scope:
  - Android application licensed under MIT.
  - Standalone printing experience.
  - Integration with the Android system printing flow.
  - Automatic printer discovery.
  - Printing over a local network and Wi-Fi Direct.
  - PDF, image, DOCX, and ODF files.
  - Print-job status tracking.
  - Simple default printing with optional advanced settings.
  - Extensible support for printer brands, protocols, formats, and connection types.
  - Support for proprietary printer protocols when required for compatibility.
- Out of scope:
  - Document scanning.
  - Internet, cloud, or remote printing.
  - User accounts or profiles.
  - Persistent print queues.
  - Print history.
  - USB printing in the initial scope.

## User Behavior

The user selects a document or image to print, chooses an automatically discovered printer, optionally adjusts advanced settings, and sends the print job. The application provides the status of the job when available.

Printing is considered successful once the job has been sent to the printer. Subsequent status information may show issues reported by the printer.

The default experience must remain simple. Advanced settings must be available without making them part of the primary flow.

## Acceptance Criteria

- [ ] A home Android user can print supported documents and images to a compatible local printer.
- [ ] The application can discover printers available on the local network.
- [ ] The application can print through Wi-Fi Direct when supported.
- [ ] The application can be used without an account, server, or cloud service.
- [ ] No user data is transmitted outside the local network.
- [ ] The application supports both standalone printing and the Android system printing flow.
- [ ] The user can access advanced print settings without complicating the default flow.
- [ ] The user can view the available status of a submitted print job.
- [ ] Printer support is extensible beyond the initial Epson validation target.

## Error and Empty States

The application must clearly communicate when no printer is discovered, when a selected printer is unavailable, when a file format is unsupported, and when the printer reports a problem.

The application must not silently fall back to Internet or cloud services when local printing is unavailable.

## Technical Notes

- The initial validation target is an Epson printer, but the product must not be coupled to Epson.
- Local network and Wi-Fi Direct are the initial connection types. USB may be added later.
- The project currently targets Android API level 24 or higher.
- The architecture should allow additional printer implementations and connection mechanisms to be added independently.

## Open Questions

- [ ] Define the detailed printer protocols and compatibility strategy in feature specifications.
- [ ] Define the detailed advanced print settings in feature specifications.
- [ ] Define the supported Android system-printing integration behavior in a feature specification.
