# Troubleshooting 

## Providers

### Issues when referencing a provider

#### Error: failed to install provider

##### Description
If you have an error during `terraform init`, similar to:
```
│ Error: Failed to install provider
│
│ Error while installing example.org/paco/foo v1.3.7: provider binary not found: could not find executable file starting with
│ terraform-provider-foo
```
##### Possible resolution
* Terraform has strict naming requirements 
* Please read through the [official Terraform](https://developer.hashicorp.com/terraform/registry/providers/publishing#manually-preparing-a-release) guide how to create your release manually.

##### Description
If you have an error during `terraform init`, similar to:
```
╷
│ Error: Failed to install provider
│
│ Error while installing example.org/XXX/XXX vX.X.X: error checking signature: openpgp: unsupported feature: public key algorithm 22
```
##### Possible resolution
* Terraform has only support for RSA and DSA keys!
* Follow the [official Terraform](https://developer.hashicorp.com/terraform/tutorials/providers/provider-release-publish#generate-gpg-signing-key) guide to create your keys manually.

##### Description

```
╷
│ Error: Failed to install provider
│
│ Error while installing example.org/XXX/XXX vX.X.X: error decoding signing key: openpgp: invalid argument: no armored data found
╵
```
##### Possible resolution
* Tapir has no valid armored public key that fits the signature for Terraform to validate the provider signature
* Run `gpg --list-keys` and verify you picked the right one. Also ensure it's a RSA/DSA key.
* ```
  --------------------------------------
    pub   rsa4096 2023-02-11 [SC]
    D17C807B4156558133A1FB843C7461473EB779BD
    uid        [ ultimativ ] Tapir Dev (The key is only used for testing purpose of Tapir) <tapir-dev@paco.vk>
    sub   rsa4096 2023-02-11 [E]
  ```
* Export the armored public key using:
* `gpg --export --armor D17C807B4156558133A1FB843C7461473EB779BD | base64`
* Add the key to Tapirs config using environment variables. Mind that the environment variables are indexed. Eg. if you have only on key the configuration for the example above would be the following. **Mind the two subsequent underscores after the index 0**
```
REGISTRY_GPG_KEYS_0__ID=D17C807B4156558133A1FB843C7461473EB779BD
REGISTRY_GPG_KEYS_0__ASCII_ARMOR=LS0tLS1CRUdJTiBQR1AgUFVCTElDIEtFWSBCTE9DSy0tLS0tCgptUUlOQkdQbjdIY0JFQURZSnpWaS9CSTZWRUdvdXpjUnd5ZUVucnZsNi82SUtEc3gzZTlTU240eDJrSkNsWEtYCnEyMmJrMFptSUpuRjkyRjZOUm54dTBoVWFPVlorb29URWZvTzFydVNtSjRMWlV4cm4zVEYramlDbHBndWQ0RzIKcGdJTHFXTlpuRnpLN3F6YncvRDVvYTU2YWRwTDRIRFB2UW5lS21qQWxzZlllQUFnNHVNdE1WSFdBbHpSU1VCcQpubDdpOG9NUUJNSmNJM2FLaktsWjhLNXN0bmFqbjZVQjFPMmV0d0ZCZ01BcVNkMXA0RTlHRnFmeC9teUxjZm9UCkEvanpaaElrSy92YnB1RHpwb2c3d3RiamowM3hUZDk5SlNuc2F3WHl2WFRoa2dKWGw4ZFVRVDRQT05HNlg3YW8KVXNZUEk3WnBURkU5RGwzTVF1TjJ5REsrS21JeEM5TTdyTUZFOG5FSFcyRnh4QTdDUHd1RVBmQnNoRFhCS2RoUwpXNlJqVlQyeFlNNUxOSi9VckxLa3BpTU9KTW91NmFSenh2UWJhVjFwZ0JBZzk3bTlFL1FyakkzVWJkTjFIRVg0CmpGZ3Zxc0I3QWVZQ0JGNVpDRGF2aHBHQ3RXUnV3L1g5TnFvdHR0UkMzbUFXTHZOZFBCSHUwZExSc2J6ZWNjZ0wKeHh0dG5YVENQNUhpcGR1Mm5jTytya1I2WkY1ejdPVDFOUnVVMk4xTXpHZUNLQzZ2WVcrVnQrMW9lbTVzd2RlcgozY0FVckZ5RTBJeXIrSzlaT1B1ekJFQjN3cVdrQ3h0WDBqTWxLNGJmcTFWNys2WFhzS1oyTDdxbVZaa3NYbjF5CmFLbC9rM2RPSWpxTUc1TG1keERSM2Q3TytrZWhkYW11aFpBRmdMM1hSQm9rNFk1d2g2TGJ1Vm9kdHdBUkFRQUIKdEZGVVlYQnBjaUJFWlhZZ0tGUm9aU0JyWlhrZ2FYTWdiMjVzZVNCMWMyVmtJR1p2Y2lCMFpYTjBhVzVuSUhCMQpjbkJ2YzJVZ2IyWWdWR0Z3YVhJcElEeDBZWEJwY2kxa1pYWkFjR0ZqYnk1MmF6NkpBbEVFRXdFSUFEc1dJUVRSCmZJQjdRVlpWZ1RPaCs0UThkR0ZIUHJkNXZRVUNZK2ZzZHdJYkF3VUxDUWdIQWdJaUFnWVZDZ2tJQ3dJRUZnSUQKQVFJZUJ3SVhnQUFLQ1JBOGRHRkhQcmQ1dlpPbUVBRFByMm1jWkNlVmVxUVZOZTFmenZhSWFKa094c2ljc0ZIcwpGRjhTalFIcGEreG9zb25ydnprSWh0M0RHUkgvVmVNN0NIeFB3TmxZQ0huMWZsZkdjWCs3eVJURmFWR1BwQ1o2Cmdlb0xyUmgvT3BIU2V4VjZDNjZseC9FUEs3U0lmWmhMR0w0aVdYU0hON2pXeTdoOW94OCtRQnNWSURIT3VNV2oKUGNjUlRxU3ZXNTFwYVozckJ0bnQ2QUg1TUVTREZiZjArMXNFQ1lRUDBhcnlNUVBEVHdDNXdTZnYyYU1rT3o5MApCZi9lYUIyOHVWa0tZeEVJdTMyQjJEclZqcG1mZldFNWpTS3JnQnlDYldoc1EyZ2c3ckFDbDJ3Z0VYNHJna0hNCml3NGhjYk13eHY1M1daaVZTaytnS2c2SzhWQVcwVjM3dU9SKzdOcktTYXZzTU9wald1Qk9GNlo5azZCSXZQbFIKNnUyd0ZubStKbHZwUHE2dnB5enR0ZnpLdlYvb1E4TSt2a2FjWTduenFpaEtNZmtYamhKdUQ4MVpVck5ONnhGKwp2ZEdaVW5TOS9jbjJjeTR3OHdtSmYvdk5WbEd2c2UvY0RaWFJzUGQ4Mk1NZkVKV1NGbkFsb1JoVHE0Qng2bE9yCnVkQnpJTk9ySm5jRkhvK0NINEZpSW0vNFd2ditSazg0djNGWGpuY1F2K3lSOXJaaE9GaWh6cUJTNGNIR1A2Q2gKRHJNTitYc3NrMTdsN0JqVHEzVGprTUlKMUVhTHlucXJyZUFRelJoR1pBR2VuaVZ2ekJJVTFvSUt1eFE4c0JmaQp5dGU1dHNYSmF6QVR0Z2k2bkY2aDJ1RFJkam8vZFp2VDd0d0VmWHQ3WER2OFIrbXQ2eDhIRnc3dk0zZGZyc3B1ClJvL2t1L1V2K0xrQ0RRUmo1K3gzQVJBQXppRXRTZDZ5MkJIOXhHais4WXpEcVZ4SzdmK2R4L1RUYldHUG9xc00KeVFERFJybUNUME1wN1hpcVhzUnVsRUJmajFMdS90aTRlUU0vb2tNUlljaS9IMW1HdldaVEt4ZHJNcWNjM0xQOQpRdlRkRnFsMkdIMVhtZFlLdnBtMGJNYTRZNWEyNU55c1hScDVMMXd0dFJsQUI5dWJVM0sycVppUFEwMjFZcmoxCm5hMjdqaW9hemFHTG9XelVxZ2JFaWVzb09LWGwvamFLQUpWZm5qZjA5OXdVUWtTN3d3TFVFWFZCeWJaekpsL3oKeVRLTnlUaG9NKzNOeGVQMzZCRzJWZERibzN2a2RXNWk2UGZOSmlxTmxGbTBOQVlzTHg5RWlSa3ZyWUFuOE0rbwpQazI1eno4ODNRMWZUSERZTlNoazhYL1BIWjlwbVlrWkFwaTdFUHFVbW1hZHd1eFgwTGJOY2lvWHV5dTEwOWtWCnY4VmhwaTVrRExpNUdETXVSZ1FrWmhxMTJVampsK3FkRUdhKzFHMWRiVWtZNzg4L3cwWnZzK21hSWZzd25TZncKbUV2bEw3MlNKdy96eHI4WDBpZ0Y5RDdpZS96K01BVWVMaDZacnA3SEVTV29JdVVxSGp6clF4ejhHemF2YndKRgpGRTNQZGIwTzV5U3hZRkF0Q2NITTJobmRZb0VOeVNpMFA5ZFlkeUNLNWZIemk2eXFBRFR0VEdXd3ZEM2F1RE83Ci8xM0hleGlZbWVzUXdXUnBzUG5IR2ducUhrVmo5MWkrZ0wwcGZWdVdLWW50ZWltUVhoUGlISmxIR1RDc0dmeE4KR3VZeTlwcXRvd1ZIYmNBSUFDNkxqWXIrYzFpQWlwTnFadjN5bThmZGx5Z0M5My9XdE1Sd2JpVzl6Y1JDdFFPNwpmbzhBRVFFQUFZa0NOZ1FZQVFnQUlCWWhCTkY4Z0h0QlZsV0JNNkg3aER4MFlVYyt0M205QlFKajUreDNBaHNNCkFBb0pFRHgwWVVjK3QzbTlEcjRRQU5jNFpyY0pKVTk0QzJJRHpzTTcxZS9oeGRrZTIvT1h4L3BpQWZJWlEyc28KUXhKSmt5Vzl5Q1czRkl1MWtJZGdoZ281OFpxTnpENVRNWTBMaEJHc3pNb1dES0praUZNZWVMdlhEY2w0TWY3UgpsV1E2NHJmczMyOS9QU2NvS2VINWFYamtQamVUNllWVENYV0xIMU5lUVp2TC9RbHhVQkVXeTlOZS9oenhJTTVFCkRUcFJWWWNmQWF0cDNFZkpRa1kxSCtyck1aaVB3ZXBLSXUrdXFucitRTU5tM3pmQjNoTWNtMEhGWFYrbDJ1SzEKNHJZRkFobjNDbm5JTE9QcWEwaHI5YlJZWmhTTjR6VVF5czZkSitrREcxeC93Q0VYSTlvcm5ucFpwcEpMaFlwNQpkQ1JsNXhXU0ZiV01RV21DZWt0N2FYakc1ZjFvb3kvT3EvWTcyQSsvWlVRelMyelNwS1czZHJiclNuMVlzV1o4CmFyTEVjRHc1dS93aWZRRVZjMmxZcndRb0czNHBnaGNsTlU4Z3dpMERXMmIxbENDVmJwa2hveHJDdHN5MXRDZ0MKeTlhUzVTM1ZPM2Qwazc2a2I0cVJobEYwcTcvS3l0K2taZjhWanpxVHJFZDdlYzBxY3RFeFZtS1JhMVBQdG55RApIUkhsNEs0ME11QjNyOXIyVm4xVnRHK3hHZDVsYmlxY3UzdEFkZytCK25QNzV5STl5OVI2NnFmRUs3TkdTTEdWClZmVkZIa3pLKzUrcENzbFdmaHEzM0IvMG1OdWZuMWJtMC9JMW9zSmJGOHUzdjljQUFwYlpCV3NTWW9ZUVdlVjkKVlg0V2FKcHZOVlViajBhcmszaUprRUJyMXlEeGNxcUxiTkJtMXdJaUMyL2s0N1VXci82OEs0SjUxQkFRV3RGSwo9c0VEQQotLS0tLUVORCBQR1AgUFVCTElDIEtFWSBCTE9DSy0tLS0tCg
```
