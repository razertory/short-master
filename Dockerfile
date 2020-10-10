# AlpineLinux with a glibc-2.23, Oracle Java 8, sbt and git
FROM anapsix/alpine-java:8_jdk

LABEL maintainer="spring.razer@gmail.com"

ENV SBT_URL=https://dl.bintray.com/sbt/native-packages/sbt
ENV SBT_VERSION 0.13.15
ENV INSTALL_DIR /usr/local
ENV SBT_HOME /usr/local/sbt
ENV PATH ${PATH}:${SBT_HOME}/bin
ENV BASE_URL http://short-master.com/r
ENV JAVA_OPTS="-Xmx512m"

RUN apk add --no-cache --update bash wget && mkdir -p "$SBT_HOME" && \
    wget -qO - --no-check-certificate "https://dl.bintray.com/sbt/native-packages/sbt/$SBT_VERSION/sbt-$SBT_VERSION.tgz" |  tar xz -C $INSTALL_DIR && \
    echo -ne "- with sbt $SBT_VERSION\n" >> /root/.built

# Install git
RUN  apk add --no-cache git openssh

# Install node.js
RUN apk add nodejs

# Copy play project and compile it
# This will download all the ivy2 and sbt dependencies and install them
# in the container /root directory


# caching dependencies
COPY ["build.sbt", "/tmp/build/"]
COPY ["project/plugins.sbt", "project/build.properties", "/tmp/build/project/"]
RUN cd /tmp/build && \
  sbt compile && \
  sbt test:compile && \
  rm -rf /tmp/build

# copy code
COPY . /root/app/
WORKDIR /root/app
RUN sbt compile && sbt test:compile

EXPOSE 9000
CMD ["sbt", "run"]


