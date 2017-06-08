#-----------------------------------------------------------------------------------------------------------------------
# File:  Dockerfile
#
# Desc:  Create a Docker container to host the Exam Report Service
#
#-----------------------------------------------------------------------------------------------------------------------
FROM frolvlad/alpine-oraclejdk8:slim

VOLUME /tmp

COPY docker-startup.sh /docker-startup.sh
COPY tds-exam-results-transmitter-*.jar /tds-exam-results-transmitter.jar

RUN sh -c 'touch /tds-exam-results-transmitter.jar /docker-startup.sh'
RUN apk --no-cache add curl

ENTRYPOINT ["/docker-startup.sh"]