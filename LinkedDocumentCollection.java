import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

//This class is a collection of LinkedDocuments.
public class LinkedDocumentCollection extends DocumentCollection {

  //epsilon for the PageRank algorithm
  public static final double PAGERANK_EPS = 0.000000001;
  
  //Empty constructor
  public LinkedDocumentCollection() {
    super();
  }

  //The specified Document is added to this collection only if it is of type LinkedDocument and 
  //if it is not already contained.
  public void prependDocument(LinkedDocument doc) {
    if (!(this.contains(doc))) {
      super.prependDocument(doc);
    }
  }

  //The specified Document is added to this collection only if it is of type LinkedDocument 
  //and if it is not already contained.
  @Override
  public void appendDocument(Document doc) {
    if ((doc instanceof LinkedDocument) && !(this.contains(doc))) {
      super.appendDocument(doc);
    }
  }

  //Private helper method that crawls this collection.
  private void crawl(LinkedDocumentCollection resultCollection) {
    if (this.numDocuments() == 0) {
      return;
    }

    /*
     * loop over all documents of this collection and add them to the in/out
     * parameter, if not already contained.
     */
    for (int i = 0; i < this.numDocuments(); i++) {
      LinkedDocument doc = (LinkedDocument) this.get(i);

      if (!resultCollection.contains(doc)) {
        resultCollection.appendDocument(doc);

        /* do the same recursively */
        doc.getOutgoingLinks(resultCollection).crawl(resultCollection);
      }
    }
  }

  //This method crawls this LinkedDocumentCollection and returns a new LinkedDocumentCollection
  public LinkedDocumentCollection crawl() {
    LinkedDocumentCollection resultCollection = new LinkedDocumentCollection();
    this.crawl(resultCollection);
    return resultCollection;
  }

  //This private helper method multiplies the specified matrix with the specified vector.
  private static double[] multiply(double[][] matrix, double[] vector) {
    if (matrix == null || vector == null) {
      return null;
    }

    double[] newVector = new double[vector.length];

    for (int i = 0; i < matrix.length; i++) {
      newVector[i] = scalarProduct(matrix[i], vector);
    }

    return newVector;
  }

  //This private helper method calculates the scalar product of the two specified vectors.
  private static double scalarProduct(double[] vectorA, double[] vectorB) {
    if (vectorA == null || vectorB == null) {
      return 0;
    }

    if (vectorA.length != vectorB.length) {
      return 0;
    }

    /* calculate the scalar product */
    double result = 0;

    for (int i = 0; i < vectorA.length; i++) {
      result += vectorA[i] * vectorB[i];
    }

    return result;
  }

  //This private helper method calculates the matrix A, which we will need to calculate the matrix M.
  private double[][] calculateMatrixA() {
    this.calculateIncomingLinks();
    
    double[][] matrixA = new double[this.numDocuments()][this.numDocuments()];

    for (int i = 0; i < this.numDocuments(); i++) {
      if (((LinkedDocument) this.get(i)).getOutgoingLinks().numDocuments() == 0) {
        for (int j = 0; j < this.numDocuments(); j++) {
          if (i != j) {
            matrixA[j][i] = 1.0d / (this.numDocuments() - 1);
          }
        }
      }

      /*
       * loop over all documents linking to document at index i and set the
       * corresponding value in matrixA
       */
      LinkedDocumentCollection incomingLinks = ((LinkedDocument) this.get(i)).getIncomingLinks();
      for (int j = 0; j < incomingLinks.numDocuments(); j++) {
        /* inLink is a document that links to the document at index i */
        LinkedDocument inLink = (LinkedDocument) incomingLinks.get(j);

        int index = this.indexOf(inLink);
        if (index >= 0) {
          matrixA[i][index] = 1.0d / inLink.getOutgoingLinks().numDocuments();
        }
      }
    }

    return matrixA;
  }

  //This private helper methods calculates the matrix M, that will be the basis for the calculation of the Page Rank values.
  private double[][] calculateMatrixM(double dampingFactor) {
    double[][] matrixM = new double[this.numDocuments()][this.numDocuments()];
    double[][] matrixA = calculateMatrixA();

    for (int i = 0; i < this.numDocuments(); i++) {
      for (int j = 0; j < this.numDocuments(); j++) {
        matrixM[i][j] = dampingFactor * matrixA[i][j] + (1 - dampingFactor) / (double) this.numDocuments();
      }
    }

    return matrixM;
  }

  //Calculates PageRank recursively. This is the entry point for the actually recursive pageRank() method
  public double[] pageRankRec(double dampingFactor) {
    int[][] C = calculateMatrixC();
    int n = C.length;

    double[] PR = new double[n];
    for (int i = 0; i < n; i++)
      PR[i] = pageRankRec(C, i, dampingFactor, 0);
    return PR;
  }

  //The positions within this collection of the documents pointing to the document at the given index position
  private int[] getIncomingDocIndices(int[][] C, int index) {
    int n = C.length, j = 0;
    int[] indices = new int[n];
    for (int i = 0; i < n; i++)
      if (C[index][i] == 1)
        indices[j++] = i;
    if (j == 0)
      for (int i = 0; i < n; i++)
        if (i != index)
          indices[j] = j++;
    return Arrays.copyOf(indices, j);
  }

  //Sums up the number of outgoing links per document based on the given link matrix C
  private int[] getNumOutgoingLinks(int[][] C) {
    int n = C.length;
    int[] c = new int[n];
    for (int i = 0; i < n; i++)
      for (int j = 0; j < n; j++)
        c[i] += C[j][i];
    return c;
  }

  //Calculates PageRank recursively.
  public double pageRankRec(int[][] C, int i, double d, int recDepth) {
    int n = C.length;
    int[] js = getIncomingDocIndices(C, i);
    int[] c = getNumOutgoingLinks(C);

    double[] PR = Arrays.copyOf(initPageRanks(), js.length);
    if (recDepth < 90)
      for (int j = 0; j < js.length; j++)
        PR[j] = pageRankRec(C, js[j], d, recDepth + 1);

    double sum = (1 - d) / n;
    for (int j = 0; j < js.length; j++)
      sum += d * PR[j] / c[js[j]];
    return sum;
  }

  //Calculates link matrix C
  private int[][] calculateMatrixC() {
    double[][] A = calculateMatrixA();
    int n = A.length;
    int[][] C = new int[n][n];

    for (int i = 0; i < n; i++)
      for (int j = 0; j < n; j++)
        if (A[i][j] != 0)
          C[i][j] = 1;

    return C;
  }

  //Generates the initial PageRank values
  private double[] initPageRanks() {
    double[] pageRanks = new double[this.numDocuments()];
    for (int i = 0; i < this.numDocuments(); i++)
      pageRanks[i] = 1.0d / this.numDocuments();
    return pageRanks;
  }

  //This method calculates the Page Rank values for all documents in this collection.
  public double[] pageRank(double dampingFactor) {
    double[][] matrixM = calculateMatrixM(dampingFactor);
    double[] pageRanks = initPageRanks();

    boolean approximationIsBad;
    do {
      approximationIsBad = false;
      double[] newPageRanks = multiply(matrixM, pageRanks);

      int i = 0;
      while (i < this.numDocuments() && !approximationIsBad) {
        if (Math.abs(newPageRanks[i] - pageRanks[i]) > PAGERANK_EPS) {
          approximationIsBad = true;
        }
        i++;
      }
      pageRanks = newPageRanks;

    } while (approximationIsBad);

    return pageRanks;
  }


  //This method calculates all incoming links for every LinkedDocument in this collection.
  public void calculateIncomingLinks() {
    
    for (int i = 0; i < this.numDocuments(); i++) {
      LinkedDocument doc = (LinkedDocument) this.get(i);
      
      for (int j = 0; j < this.numDocuments(); j++) {
        LinkedDocument incomingDoc = (LinkedDocument) this.get(j);
        if (incomingDoc.getOutgoingLinks().contains(doc)) {
          doc.addIncomingLink(incomingDoc);
        }
      }
    }
  }


  //Returns a string representation of this LinkedDocumentCollection using the IDs of the LinkedDocuments.
  public String toString() {
    if (this.numDocuments() == 0) {
      return "[]";
    }

    if (this.numDocuments() == 1) {
      return "[" + ((LinkedDocument) this.get(0)).getID() + "]";
    }

    String res = "[";
    for (int i = 0; i < this.numDocuments() - 1; i++) {
      res += ((LinkedDocument) this.get(i)).getID() + ", ";
    }
    res += ((LinkedDocument) this.get(this.numDocuments() - 1)).getID() + "]";
    return res;
  }

  //Sorts this instance descending by the relevance of the contained LinkedDocuments.
  private double[] sortByRelevance(double dampingFactor, double weightingFactor) {
    double[] pageRanks = this.pageRank(dampingFactor);
    double[] relevance = new double[this.numDocuments()];
    LinkedDocument[] docMap = new LinkedDocument[this.numDocuments()];

    for (int i = 0; i < this.numDocuments(); i++) {
      relevance[i] = weightingFactor * this.getQuerySimilarity(i) + (1 - weightingFactor) * pageRanks[i];
      docMap[i] = (LinkedDocument) this.get(i);
    }

    /* bubblesort(descending)*/
    for (int i = 1; i < this.numDocuments(); i++) {
      for (int j = 0; j < this.numDocuments() - i; j++) {
        if (relevance[j] < relevance[j + 1]) {
          double tmpRel = relevance[j];
          relevance[j] = relevance[j + 1];
          relevance[j + 1] = tmpRel;

          LinkedDocument tmpDoc = docMap[j];
          docMap[j] = docMap[j + 1];
          docMap[j + 1] = tmpDoc;
        }
      }
    }

    /* create new collection containing documents in order according to relevance */
    for (int i = 0; i < this.numDocuments(); i++) {
      this.remove(this.indexOf(docMap[i]));
      this.appendDocument(docMap[i]);
    }

    return relevance;
  }

  //This method sorts this collection by relevance
  public double[] match(String query, double dampingFactor, double weightingFactor) {
    super.match(query);

    return this.sortByRelevance(dampingFactor, weightingFactor);
  }

  //Finds a LinkedDocument with the given id if contained in this LinkedDocumentCollection
  public LinkedDocument findByID(String id) {
    for (int i = 0; i < this.numDocuments(); i++)
      if (((LinkedDocument) this.get(i)).getID().equals(id))
        return ((LinkedDocument) this.get(i));
    return null;
  }
}
