SOLVER_HOME=/opt/solver/CUDASieve/lib
MODULUS=""

while [ $# -gt 1 ] || [ $# -eq 1 ]; do
    MODULUS="$1 $MODULUS"
    shift;
done

MAX=$(echo $MODULUS | perl -ne 's/\s+/\n/g && print $_' | sort -r | head -n 1)
N=$(echo $MODULUS | perl -ne 's/\s+/\n/g && print $_' | wc -l | perl -ne '/(\d+)/ && print $1')

MODULUS="$N $MODULUS"
 
mkdir $MAX && cd $MAX
python $SOLVER_HOME/slides.py 100000 $MAX | shuf >  slides.txt
 
COUNTER=0
while read p; do
    echo $COUNTER
    BOTTOM=$(echo $p | perl -F, -ane 'print $F[0]')
    TOP=$(echo $p | perl -F, -ane 'print $F[1]')
    $SOLVER_HOME/solver $BOTTOM $TOP $MODULUS
    COUNTER=$((COUNTER + 1))
done < slides.txt >> done.csv
