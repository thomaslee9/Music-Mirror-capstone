package com.mm.v1.semanticmatch;

import java.util.List;

public class Vector {

    private Double[] array;
    private int size;

    public Vector(List<Double> data)    {

        this.array = data.toArray(new Double[0]);
        this.size = data.size();

    }

    public Double[] GetArray()  {
        return this.array;
    }

    public int GetSize()    {
        return this.size;
    }

    /**
     * cosinesim(A, B) where A and B are vectors
     * := dotproduct(A, B) / (norm(A) * norm(B))
     */
    public Double CosineSimilarity(Vector other)    {

        Double dotproduct = this.DotProduct(other);
        Double similarity = dotproduct / (this.Norm() * other.Norm());

        return similarity;

    }

    public Double DotProduct(Vector other)  {

        Double result = 0.0;

        for (int i = 0; i < other.GetSize(); i++)   {
            result += (this.GetArray()[i] * other.GetArray()[i]);
        }

        return result;

    }

    public Double Norm()    {

        Double sumOfSquared = 0.0;

        for (int i = 0; i < this.GetSize(); i++)   {

            sumOfSquared += (this.GetArray()[i] * this.GetArray()[i]);

        }  

        return Math.sqrt(sumOfSquared);

    }

    public void Print()  {

        for (Double e : this.array) {
            System.out.println(e);
        }

    }
    
}
