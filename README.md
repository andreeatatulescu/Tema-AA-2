# HOMEWORK 2 AA 

## About

Algorithm Analysis Course

January 2021

Student: Tatulescu Diana-Andreea, 321CD

## Run tests

First of all : ./setup.sh

Run checker: python3.7 ./checker.py java <task1> <task2> <task3> <bonus>

### Task1

This is based on K-coloring problem, where the number of families is number of nodes,
number of relationships between families is the number of edges and the spies are the
possible colours for each node. In order to solve this task, I have stored the edges
fiven from input in a TreeMap and generated a matrix [N][K], where N is the number 
of nodes and K the number of spies, thus N * K variables representing my SAT reduction.

After reading the input, in formulateOracleQuestion(), I have started to build the 
clauses - stored by me in a StringBuilder clauses. The first step was to calculate 
the number of clauses in order to fill the first line of the .cnf file. After this, 
I have generated using the edges map and the SAT reduction the 3 types of clauses:

* Each family needs to have a spy assigned.
(one clause = one line of SAT reduction)

* Each family must have MAXIMUM a spy, so we cannot have 2 spies for the same family.
(combinations of K by 2 on matrix lines)

* If 2 families get along, they must have different spies.
(combination between "friends" families)

After forming the clauses, the Oracle gives the answer and all we have to do is to 
give an interpretation: for this task the variables > 0 were the spies assigned
and we have to see what spy is it (the K-th spy) and write down our solution in
the .out file.

### Task2

Based on Clique Problem, very similar to the previous one, the difficulty comes
just when we talk about possible duplicate clauses and the fact that we used 
need the non-edges map, so the families which do not get along. For this, 
after generated the first set of clauses, I have formed a list for each variable
with all its combinations in order to eliminate the case of having 2 identical
clauses. Talking about the SAT reduction used, the families are the columns, 
having K members - the cliqueDimension.

* An extended family mush have mandatorily K members.
(one clause = one line of SAT reduction)

* Two families can not be at the same time in the same K-th position in the 
extended family.
(combinations on matrix lines)

* A family can not be on 2 position in the same extended family at the same time.
(combination on columns)

* Families which do not get alonng cannot belongto the same extended family
at the same time.
(this is why we used non-edges)

This time the interpretation is that all the variables > 0 form an extended
family and we have to see which family they belong to.

### Task3

The K-Vertex cover is somehow the complement of Task2, so all we have to do is
to generate the complement of our graph (in my case the complemnt of the 
edges map) and call task2 using these. We want the arrest the minimum possible
in order to kill the Mafia, so in order to do that I have used a for instruction
from the maximum number of families to 0, waiting for the Oracle to return
the first answer containing "True". Because in this task we want always the
complementary, in order to interpretate the Oracle's answer we have to 
write down in .out file those families which do not appear in the answer 
from task2.

### Bonus

From what I have read about Weigthed Partial Max-SAT, there are 2 types of clauses,
hard ones and fot ones. The weight for the hard ones is sum of weights from softs + 1,
this is usually sufficient and for the soft clauses, there could be more formulas, 
but important is that while the weight is greater than 1 (never zero!) and the
sum of soft weights is not > 2^63, the things are good. I was looking for a formula
for this, but unfortunatelly I didn't found one, so what I have done was to give 
the weight 1 to all soft clauses, thus the top = nrFamilies + 1.

* Soft clauses for nodes

* Hard clauses for relations between nodes

## Difficulties

At first, it was a ittle unclear how Oracle works and how are we supposed to
generate the clauses, which were not that easy to "translate". The examples 
from homework's pdf could have been more relevant. More than that, for the
Bonus part, for my implementation, the interpretation is the opposite from 
that on pdf, that't why am not so sure about my solution.

## Useful Materials

* https://www.youtube.com/watch?v=HhFSgXbWiWY
* https://www.cs.cmu.edu/~emc/15-820A/assignments/solution1.pdf
* huntaj.stu.cofc.edu/web/AHuntExtraCredit310.pdf
* https://www.clear.rice.edu/comp487/VC_Clique.pdf






