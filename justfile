release-dryrun:
    ./mvnw -Prelease jreleaser:full-release -Djreleaser.select.current.platform -Djreleaser.dry.run=true

jrelease:
    ./mvnw -Prelease jreleaser:full-release -Djreleaser.select.current.platform 