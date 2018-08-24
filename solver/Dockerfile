FROM nvidia/cuda
RUN apt-get update && apt-get install -y git clang wget libgmp-dev libgmpxx4ldbl
RUN mkdir /opt/solver

WORKDIR /opt/solver
RUN git clone https://github.com/curtisseizert/CUDASieve

WORKDIR /opt/solver/CUDASieve
RUN mkdir lib

WORKDIR /opt/solver/CUDASieve/lib
RUN wget https://bintray.com/curtisseizert/CUDASieve/download_file?file_path=libcudasieve.a -O libcudasieve.a
COPY main.cpp /opt/solver/CUDASieve/lib/main.cpp
COPY runner.sh /opt/solver/CUDASieve/lib/runner.sh
COPY slides.py /opt/solver/CUDASieve/lib/slides.py
