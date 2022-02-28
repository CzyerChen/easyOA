FROM nginx:1.14.2

MAINTAINER claire<6323****@qq.com>

ENV  TIME_ZONE Asia/Shanghai

RUN /bin/cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime

RUN rm /etc/nginx/conf.d/default.conf

ADD default.conf /etc/nginx/conf.d/

COPY dist/  /usr/share/nginx/html/
