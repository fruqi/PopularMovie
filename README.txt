
> Put your Movie DB api key under build.gradle (app), under the following block to be specific:

    buildTypes.each {
        it.buildConfigField('String', 'MOVIE_DB_API_KEY', '"<PUT YOUR KEY HERE>"')
    }