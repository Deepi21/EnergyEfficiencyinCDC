import java.io.*;
import java.util.*;
import py4j.GatewayServer;
/**
 * Write a description of class Fault here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Fault
{

    // instance variables - replace the example below with your own
    public static void main(String []args)throws IOException
    { 
	  Fault app = new Fault();
	  GatewayServer gatewayServer = new GatewayServer(app);
	  gatewayServer.start();
	  System.out.println("In main");
    }
    public static void reliabilityRequirement()throws IOException
    {
		System.out.println("In rr");
		int u_size, n_size;
		double R_req_G;
		BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
		System.out.println("enter the number of VMs");
		u_size=Integer.parseInt(br.readLine());
		System.out.println("Enter the number of tasks in the workflow G");
		n_size=Integer.parseInt(br.readLine());
		double w[][] = CloudsimSimulation.Execution_time(u_size, n_size);
		u_size++;
		n_size++;
		System.out.println("Enter the Reliability Requirement of the workflow G");
		R_req_G=Double.parseDouble(br.readLine());
		// int w[][]=new int[n_size][u_size];
		//int w[][]={{0,0,0,0},{0,14,16,9},{0,13,19,18},{0,11,13,19},{0,13,8,17},{0,12,13,10},{0,13,16,9},{0,7,15,11},{0,5,11,14},{0,18,12,20},{0,21,7,16}};
		int c[][]=new int[n_size][n_size];
		double avg_w[]=new double[n_size];
		double rank[]=new double[n_size];
		int succ[][]=new int[n_size][];
		int pred[][]=new int[n_size][];
		int entry=1,exit=n_size-1;
		succ[exit]=new int[0];
		pred[entry]=new int[0];
		for(int i=1;i<n_size;i++){
			int sum=0;
			for(int j=1;j<u_size;j++){
		   /* System.out.println("Enter the execution time of n["+i+"] for u["+j+"]");
			w[i][j]=Integer.parseInt(br.readLine());*/
				sum+=w[i][j];
			}
		    avg_w[i]=(double)sum/(u_size-1);     
		}
		for(int i=1;i<n_size-1;i++){
			System.out.println("Enter the number of successors for task n["+i+"]");
			int num_succ=Integer.parseInt(br.readLine());
			succ[i]=new int[num_succ+1];
			for(int j=1;j<=num_succ;j++){
				System.out.println("Enter the successor for task n["+i+"]");
				succ[i][j]=Integer.parseInt(br.readLine());
			}
		}
		for(int i=1;i<n_size;i++){
			for(int j=1;j<succ[i].length;j++){
					System.out.println("Enter the cost of communication between n["+i+"] for n["+succ[i][j]+"]");
					c[i][succ[i][j]]=Integer.parseInt(br.readLine());
			}
		} 
		for(int i=2;i<n_size;i++){
			int no_of_pred=0;
			for(int j=1;j<n_size;j++){
				if(c[j][i]!=0)no_of_pred++;
			}
			pred[i]=new int[no_of_pred+1];
			int k=1;
			for(int j=1;j<n_size;j++){
				if(c[j][i]!=0)pred[i][k++]=j;
			}
			/*System.out.println("Enter the number of predecessors for task n["+i+"]");
			int num_pred=Integer.parseInt(br.readLine());
			pred[i]=new int[num_pred+1];
			for(int j=1;j<=num_pred;j++){
					System.out.println("Enter the predecessor for task n["+i+"]");
					pred[i][j]=Integer.parseInt(br.readLine());
			}*/
		}    
		  
		rank[exit]=avg_w[exit];
		for(int i=n_size-1;i>0;i--){
			double max=Integer.MIN_VALUE;
			for(int j=1;j<succ[i].length;j++){
				if((c[i][succ[i][j]]+rank[succ[i][j]])>max){
					max=c[i][succ[i][j]]+rank[succ[i][j]];
				}
				rank[i]=avg_w[i]+max;
			}
		}
		for(int i=1;i<n_size;i++){
			System.out.println(rank[i]);
		}
		rank_number arr[]=new rank_number[n_size];
		for(int i=1;i<n_size;i++){
			arr[i]=new rank_number(i,rank[i]);
		}
		Arrays.sort(arr,1,arr.length,new SortbyRank());
		for(int i=1;i<n_size;i++){
			System.out.println(arr[i].index+" "+arr[i].rank);
		}
		 //--step 1 completed
		double R_up_req=Math.pow(R_req_G,(1.0/(n_size-1)));      //eqn 13
		double sub_req[]=new double[n_size];
		int num_replicas[]=new int[n_size];
		double reliability[][]=new double[n_size][u_size];
		double rel_total[]=new double[n_size];
		double cost[]=new double[n_size];
		double lam[]={0.001,0.002,0.003};
		double gamma[]={2,1.5,1};
		int mark[][]=new int[n_size][u_size];
		for(int i=1;i<n_size;i++){
			double prev= 1.0;
			double prod1=1,prod2;
			for(int j=i-1;j>=1;j--){
				prod1*=rel_total[j];
			}
			prod2=Math.pow(R_up_req,n_size-i-1);
		    sub_req[i]=R_req_G/(prod1*prod2);
			for(int k=1;k<u_size;k++){
				reliability[i][k]=Math.exp(-(lam[k-1]*w[i][k]));
			}
			while(rel_total[i]<sub_req[i]){
				double min=(double)Integer.MAX_VALUE; int pos=-1;
				for(int k=1;k<u_size;k++){
					if(mark[arr[i].index][k]==0){
						if(min>w[arr[i].index][k]){
							min=w[arr[i].index][k];
							pos=k;
						}
					}
				}
				mark[arr[i].index][pos]=1;
				num_replicas[i]++;
				System.out.println("Replica:"+num_replicas[i]+"of task no "+i+" Scehduled on "+pos+" VM with "+w[arr[i].index][pos]+"time");
				rel_total[i]=1-(prev)*(1-reliability[i][pos]);
				prev*=(1-reliability[i][pos]);
				cost[i]+=w[arr[i].index][pos]*gamma[pos-1];
			}
		}
		int total_number_of_replicas=0;
		double total_cost=0;
		double total_reliability_G=1.0;
		for(int i=1;i<n_size;i++){
			total_number_of_replicas+=num_replicas[i];
			total_cost+=cost[i];
			total_reliability_G*=rel_total[i];
		}
				
		System.out.println(total_number_of_replicas);
		System.out.println(total_cost);
		System.out.println(total_reliability_G);        
    }
}
        
    
        
    

