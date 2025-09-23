# Install

Follow https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html

# SSO

Follow https://docs.aws.amazon.com/cli/latest/userguide/cli-configure-sso.html

Example (Get start url from IAM identity centre.):
```
$ aws configure sso --use-device-code
SSO session name (Recommended): my-sso
SSO start URL [None]: https://my-sso-portal.awsapps.com/start
SSO region [None]: eu-west-2
SSO registration scopes [None]: sso:account:access
```
# Profile

Create and manage named AWS CLI profiles so you can switch between accounts or roles.

1) Create a profile interactively

```bash
aws configure --profile myprofile
```

This prompts for AWS Access Key ID, Secret Access Key, default region and output format and saves credentials to `~/.aws/credentials` and profile configuration to `~/.aws/config`.

2) Add a profile manually (edit files)

`~/.aws/credentials`:

```
[myprofile]
aws_access_key_id = YOUR_ACCESS_KEY_ID
aws_secret_access_key = YOUR_SECRET_ACCESS_KEY
```

`~/.aws/config`:

```
[profile myprofile]
region = us-east-1
output = json
```

3) Use a profile for a single command

```bash
aws s3 ls --profile myprofile
```

4) Set profile for your shell session

```bash
export AWS_PROFILE=myprofile
# Then run commands without passing --profile
aws sts get-caller-identity
```

5) List available profiles

```bash
aws configure list-profiles
```

6) Profiles that assume a role / MFA

Configure role assumption in `~/.aws/config`:

```
[profile dev]
role_arn = arn:aws:iam::123456789012:role/Developer
source_profile = default
region = us-east-1
```

If MFA is required, obtain temporary credentials with `aws sts get-session-token` or use a credential helper. Example (temporary creds):

```bash
aws sts get-session-token --serial-number arn-of-mfa-device --token-code 123456 --profile default
```

Verification commands

```bash
aws sts get-caller-identity --profile myprofile
aws configure list --profile myprofile
```

Notes
- Credentials: `~/.aws/credentials`
- Profile config: `~/.aws/config` (profiles are prefixed with `profile ` in this file)
- Never commit these files to source control.