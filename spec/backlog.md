# Impresso Product Backlog

This backlog is derived from [product-overview.md](product-overview.md). Priorities are proposed for implementation planning.

## Stories

GitHub Project is the source of truth for the delivery status. The status below was synchronized on 2026-09-17.

| ID | GitHub issue | Status | Priority | Story | Acceptance criteria |
| --- | --- | --- | --- | --- | --- |
| IMP-01 | [#5](https://github.com/zemoa/Impresso/issues/5) | Done | P0 | As a user, I want to select a document or image so that I can print it. | [Functional specification](01-select-printable-file.md). PDF, image, DOCX, and ODF files are accepted. The selected file is visible before submission. |
| IMP-02 | [#3](https://github.com/zemoa/Impresso/issues/3) | Backlog | P0 | As a user, I want to print through the app's standalone flow. | I can start a print job without another app. The default flow remains simple. |
| IMP-03 | [#1](https://github.com/zemoa/Impresso/issues/1) | Done | P0 | As a user, I want the app to automatically discover printers on my local network. | Compatible printers available on the local network are displayed. No Internet search is performed. |
| IMP-04 | [#4](https://github.com/zemoa/Impresso/issues/4) | Ready | P0 | As a user, I want to select a discovered printer. | The list provides enough information to distinguish printers. I can select a printer before submission. |
| IMP-05 | [#14](https://github.com/zemoa/Impresso/issues/14) | Backlog | P0 | As a user, I want to be informed when no printer is found. | A clear empty state is displayed. The app does not silently fall back to a cloud service. |
| IMP-06 | [#9](https://github.com/zemoa/Impresso/issues/9) | Backlog | P1 | As a user, I want to print through Wi-Fi Direct when supported. | A compatible Wi-Fi Direct printer can be discovered and selected. The document can be sent without a conventional local network. |
| IMP-07 | [#2](https://github.com/zemoa/Impresso/issues/2) | Ready | P0 | As a user, I want to submit a print job using default settings. | I can submit the file without configuring advanced settings. Printing is successful once the job has been sent to the printer. |
| IMP-08 | [#6](https://github.com/zemoa/Impresso/issues/6) | Backlog | P1 | As a user, I want to access advanced settings before printing. | Advanced settings are accessible from the print flow. They do not complicate the default flow. |
| IMP-09 | [#8](https://github.com/zemoa/Impresso/issues/8) | Ready | P0 | As a user, I want to be informed when the selected printer is unavailable. | [Functional specification](06-report-unavailable-selected-printer.md). Submission is blocked or clearly reported before acceptance, and I can return to printer selection. |
| IMP-10 | [#10](https://github.com/zemoa/Impresso/issues/10) | Backlog | P0 | As a user, I want to view the status of a submitted print job. | The available job status is displayed. Missing status information is distinguished from an error. |
| IMP-11 | [#7](https://github.com/zemoa/Impresso/issues/7) | Backlog | P0 | As a user, I want to be informed when the printer reports a problem. | The problem reported by the printer is clearly communicated. |
| IMP-12 | [#13](https://github.com/zemoa/Impresso/issues/13) | Backlog | P0 | As a user, I want to print through the Android system print flow. | Impresso is available in Android's print flow. A document submitted through that flow can be selected and sent using the same printing capabilities. |
| IMP-13 | [#12](https://github.com/zemoa/Impresso/issues/12) | Backlog | P0 | As a user, I want to use the app without an account or server. | No account is required. Printing works without a remote or cloud service. |
| IMP-14 | [#15](https://github.com/zemoa/Impresso/issues/15) | Ready | P0 | As a user, I want my documents to remain on the local network. | [Functional specification](14-keep-documents-local.md). No document or print data is transmitted outside the local network. |
| IMP-15 | [#11](https://github.com/zemoa/Impresso/issues/11) | Backlog | P1 | As the product team, we want to add brands, protocols, formats, and connection types over time. | Initial Epson support does not block support for other printers. Printer implementations and connection mechanisms can evolve independently. |

## Recommended Order

1. IMP-01, IMP-03, IMP-04, IMP-07, IMP-09
2. IMP-05, IMP-10, IMP-11, IMP-13, IMP-14
3. IMP-02, IMP-12
4. IMP-06, IMP-08, IMP-15

## Functional Validation Checklist

- [ ] Select a PDF, image, DOCX, and ODF file.
- [ ] Attempt to select an unsupported file format.
- [ ] Discover an Epson printer on the local network.
- [ ] Verify behavior with multiple printers.
- [ ] Verify behavior when no printer is available.
- [ ] Select a printer, make it unavailable, and try to submit a job.
- [ ] Submit a print job using default settings.
- [ ] Open and use the advanced settings.
- [ ] Verify status display after submission.
- [ ] Trigger or simulate an error reported by the printer.
- [ ] Print through Android's system print flow.
- [ ] Verify that no account or Internet connection is requested.
- [ ] Verify that no data leaves the local network.
- [ ] Test Wi-Fi Direct printing when suitable hardware is available.

## Open Product Decisions

- Define the exact image extensions or MIME types supported.
- Define the advanced settings: copies, orientation, color, duplex, paper size, page range, and others.
- Define the minimum job statuses and their user-facing terminology.
- Define network discovery refresh behavior, timeout, and whether manual printer entry is needed.
- Define the Wi-Fi Direct connection flow and required Android permissions.
- Define the exact Android system print integration behavior and limitations.
- Define the protocols supported in the first version and the Epson compatibility strategy.
