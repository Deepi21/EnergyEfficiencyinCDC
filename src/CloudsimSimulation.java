import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmScheduler;
import org.cloudbus.cloudsim.VmSchedulerSpaceShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;
/**
 * 
 */

/**
 * @author Anupriya Gupta
 *
 */
public class CloudsimSimulation {

	/**
	 * @param args
	 */
	
	public static void start(int vmId,List<Random> randomList, int u_size, int n_size ,double w_matrix[][]) {
		//1.0: Initialize the CloudSim package. 
		//It should be called before creating any entities. '
		
		int numUser = 1;
		Calendar cal = Calendar.getInstance();
		boolean traceFlag = false;
		CloudSim.init(numUser, cal, traceFlag);
		
		
		
		//2.0: Create Datacenter: Datacenter k-- Datacentercharacteristics 
		//K-- HostList K-- Processing element List, Also Defines policy for VM allocation and scheduling 
		Datacenter dc = CreateDataCenter();
		
		
		
		//3.0: Create Broker 
		DatacenterBroker dcb = null;
		try {
			dcb = new DatacenterBroker("DatacenterBroker1");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		//4.0: Create Cloudlets:Defines the workload 
		List<Cloudlet> cloudletList = new ArrayList<Cloudlet>();
		int cloudletLength = 1000; 
		int pesNumber = 1; 
		int cloudletFileSize = 300; 
		int cloudletOutputSize = 400; 
		UtilizationModelFull fullUtilize = new UtilizationModelFull();
		for(int cloudletId = 0; cloudletId<n_size; cloudletId++){
			Random rOjb = randomList.get(cloudletId);
			Cloudlet c = new Cloudlet(cloudletId, (cloudletLength + rOjb.nextInt(3000)), pesNumber, cloudletFileSize, 
				cloudletOutputSize, fullUtilize, fullUtilize, fullUtilize);
			c.setUserId(dcb.getId());
			cloudletList.add(c);
		}
		
		
		
		
		//5.0: Create VMs:Define the procedure for Task scheduling algorithm 
		
		int mips = 1000; 
		int numberOfPes =1;
		int ram = 2000; 
		int bw = 1000; 
		int diskSize = 20000; 
		String vmm = "XEN";
		List<Vm> vmList =  new ArrayList<Vm>();
		Random rram= new Random();
		Random rbw = new Random();
		Random rdiskSize = new Random();
		Vm virtualMachine = new Vm(vmId, dcb.getId(),mips, 
					numberOfPes, (ram + rram.nextInt(5000)), (bw +rbw.nextInt(2000)), 
					(diskSize +rdiskSize.nextInt(30000)), vmm, new CloudletSchedulerSpaceShared());
		vmList.add(virtualMachine);
		Log.printLine(virtualMachine.getId());
		Log.printLine(vmList.get(0));
		dcb.submitCloudletList(cloudletList);
		dcb.submitVmList(vmList);
		
		//6.0: Starts the simulation: Automated process,
		//handled through descreted event simulation engine 
		CloudSim.startSimulation();
		
		List<Cloudlet> finalCloudExecutionResults = dcb.getCloudletReceivedList();
		
		CloudSim.stopSimulation();
		
		//7.0: Print results when simulation is over as Outputs
		int cloudletno = 1;
		for(Cloudlet c: finalCloudExecutionResults)
		{
			Log.printLine("Result of Cloulet No"+cloudletno);
			Log.printLine("********************************************************");
			Log.printLine("ID:"+ c.getCloudletId()+ " VM:" +c.getVmId()+ " Status:" +c.getStatus()
					+ " ExecutionTime:" + c.getActualCPUTime()+ " Start" + c.getExecStartTime()+
					" Finish" + c.getFinishTime()+ " CPUutilization" + c.getUtilizationOfCpu(c.getActualCPUTime()));
			Log.printLine("********************************************************");
			
			w_matrix[cloudletno][vmId] = c.getActualCPUTime();
			
			cloudletno++;
			
		}
		
	}
		
	
	private static Datacenter CreateDataCenter() 
	{
		List<Pe> peList = new ArrayList<Pe>();
		PeProvisionerSimple peProvisioner = new PeProvisionerSimple(1000);
		
		Pe core1 = new Pe(0, peProvisioner);
		peList.add(core1);
		Pe core2 = new Pe(1, peProvisioner);
		peList.add(core2);
		Pe core3 = new Pe(2, peProvisioner);
		peList.add(core3);
		Pe core4 = new Pe(3, peProvisioner);
		peList.add(core4);
		
		List<Host> hostList = new ArrayList<Host>();
		int ram = 8000;
		int bw = 8000;
		long storage = 100000;
		
		Host host1 = new Host(0, new RamProvisionerSimple(ram), 
				new BwProvisionerSimple(bw), storage, peList, new VmSchedulerSpaceShared(peList));
		hostList.add(host1);
		Host host2 = new Host(1, new RamProvisionerSimple(ram), 
				new BwProvisionerSimple(bw), storage, peList, new VmSchedulerSpaceShared(peList));
		hostList.add(host2);
		Host host3 = new Host(2, new RamProvisionerSimple(ram), 
				new BwProvisionerSimple(bw), storage, peList, new VmSchedulerSpaceShared(peList));
		hostList.add(host3);
		Host host4 = new Host(3, new RamProvisionerSimple(ram), 
				new BwProvisionerSimple(bw), storage, peList, new VmSchedulerSpaceShared(peList));
		hostList.add(host4);
		
		
		String architecture = "x86"; 
		String os = "Linux"; 
		String vmm = "XEN";
		double timeZone = 5.0; 
		double ComputecostPerSec = 3.0; 
		double costPerMem = 1.0; 
		double costPerStorage = 0.05; 
		double costPerBw = 0.10;
		DatacenterCharacteristics dcCharacteristics = new DatacenterCharacteristics(architecture, os, vmm, hostList, timeZone, 
				ComputecostPerSec, costPerMem, costPerStorage, costPerBw);
		
		LinkedList<Storage> SANstorage = new LinkedList<Storage>();
		Datacenter dc = null;
		
		try {
			dc = new Datacenter("Datacenter1", dcCharacteristics, new VmAllocationPolicySimple(hostList), SANstorage, 1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return dc;
	}
	
	public static double[][] Execution_time(int u_size, int n_size){
		List<Random> randomList = new ArrayList<Random>();
		double[][] w_matrix = new double[n_size+1][u_size+1];
		for(int i=0;i<n_size;i++)
		{
			Random rOjb = new Random(); 
			randomList.add(rOjb);
		}
		for(int i=1;i<=u_size;i++){
			start(i,randomList,u_size,n_size,w_matrix);
		}
	
		return w_matrix;
		}

}

