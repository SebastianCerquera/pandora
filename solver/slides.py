import sys
import math
 
n = int(sys.argv[1])
p = int(sys.argv[2])
 
s = math.ceil(p**(1.0/2))
s = s/n
s = int(s + 6 - (s % 6))
 
for i in range(1, n+1):
    print "{},{}".format(s*i,s*(i+1))
