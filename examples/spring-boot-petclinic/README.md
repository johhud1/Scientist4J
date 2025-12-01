# Scientist4J Spring Boot PetClinic Example

This example demonstrates integrating Scientist4J with a Spring Boot web application.

## Prerequisites

- Java 21
- Maven 3.9+

## Build & Run

From this directory, run:

```bash
mvn spring-boot:run
```

The application starts on <http://localhost:8080/>.

Available routes:

- `/` – landing page
- `/owners` – list of sample owners
- `/owners/{id}` – owner detail page
- `/vets` – veterinarians directory
- `/actuator/metrics` – Micrometer metrics (enabled via Actuator)
- `/actuator/health` – health probe
- `/actuator/info` – basic app info
- `/actuator/prometheus` – Prometheus scrape endpoint

### Containerized stack with Prometheus + Grafana

To run the application alongside Prometheus, build the Docker image and launch via Compose:

```bash
docker compose build
docker compose up
```

Docker builds leverage BuildKit’s cache mount for Maven artifacts, so repeat builds reuse dependencies automatically (`DOCKER_BUILDKIT=1` is recommended).

The app remains on <http://localhost:8080/>, Prometheus UI is available at <http://localhost:9090/>, and Grafana is served at <http://localhost:3000/> (`admin`/`admin`). Grafana auto-loads the Prometheus datasource and a basic Scientist4J PetClinic dashboard. Prometheus scrapes `/actuator/prometheus` per `prometheus.yml`.

## Scientist4J Usage

`PetClinicService` wraps the owner lookup with a simple `Experiment`. Both control and candidate share the same repository call here, illustrating how to wire Scientist4J into service logic. Replace the candidate function with a new implementation to compare behavior during real migrations.
