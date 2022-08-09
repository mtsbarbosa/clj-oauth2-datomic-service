# clj-oauth2-datomic-service

FIXME

## Getting Started

1. Install and run local free-datomic:
```shell
docker run -d -e ADMIN_PASSWORD="admin" -e DATOMIC_PASSWORD="datomic" -p 4334-4336:4334-4336 --name datomic-free akiel/datomic-free
```
2. For pro datomic, follow the docs: [Get Datomic](https://www.datomic.com/get-datomic.html)
3. This app uses [Twilio SendGrid](https://docs.sendgrid.com/pt-br/) to send emails
   1. Register, generate API KEY and add your company sender emails to Sender Authentication
   2. Case you want to use a different e-mail server, adapt `ports.mail.client`
4. Start the application: `export CLJ_OAUTH2_MAIL_API_KEY=<your email provider api key> && lein with-profile dev run`
5. Start dev server: `export CLJ_OAUTH2_MAIL_API_KEY=<your email provider api key> && lein run-dev-w-migration` or `lein run-dev`
6. Go to [localhost:8080](http://localhost:8080/breads) to see a nice list of breads in json!
7. Read your app's source code at src/clj-oauth2-datomic-service/service.clj. Explore the docs of functions
   that define routes and responses.
8. Edit your datomic schema at `ports/datomic/schema.clj` as you wish.
9. Run your app's tests with `lein test`.
10. Learn more! See the [Links section below](#links).


## Configuration

To configure logging see config/logback.xml. By default, the app logs to stdout and logs/.
To learn more about configuring Logback, read its [documentation](http://logback.qos.ch/documentation.html).


## Developing your service

1. Start a new REPL: `lein repl`
2. Start your service in dev-mode: `(def dev-serv (run-dev))`
3. Connect your editor to the running REPL session.
   Re-evaluated code will be seen immediately in the service.

### [Docker](https://www.docker.com/) container support

1. Configure your service to accept incoming connections (edit service.clj and add  ::http/host "0.0.0.0" )
2. Build an uberjar of your service: `lein uberjar`
3. Build a Docker image: `sudo docker build -t clj-oauth2-datomic-service .`
4. Run your Docker image: `docker run -p 8080:8080 clj-oauth2-datomic-service`
