/*Jason Grant
HITS Algorithm Project*/
import java.util.*;
import java.net.*;
import java.io.*;

public class Main {
	private static String searchTerm; //store user input search term
	private static List<Link> graph= new ArrayList<>();//store root/neighborhood graph
	private static int kLimit;//user input k value for max number of links found on root page
	private static int nRank;//user input value for number of pages listed in hub/authority rankings
	private static int iterat;//testing for how many iterations needed to complete HITS algorithm


	public static void main(String[] args) throws Exception {
		Scanner scanner = new Scanner(System.in);//scan for user input

		System.out.println("Hey there, please input your desired Yahoo Search Term:  ");
		System.out.println();
		String search = scanner.nextLine();

		searchTerm =search.replaceAll("\\s+","+");//replace spaces with + to fit yahoo search format

		System.out.println("Please enter k value for maximum amount of links for any root page:  ");
		System.out.println();
		kLimit = Integer.parseInt(scanner.nextLine());

		System.out.println("Please enter N value for number of web pages to rank:  ");
		System.out.println();
		nRank = Integer.parseInt(scanner.nextLine());

		scanner.close();

		buildRoot(searchTerm);
		buildNeighborhood();

		//buildTestGraph();

		hitsCounter();

		System.out.println();
		System.out.println("There are "+graph.size()+" web pages in this graph");

		System.out.println();
		System.out.println("Here are the top "+nRank+" web pages ranked by their Authority Weights: ");
		rankEmAuth();
		printRankedGraph();
		System.out.println();

		System.out.println("Here are the top "+nRank+" web pages ranked by their Hub Weights: ");
		rankEmHub();
		printRankedGraph();
		System.out.println();

		System.out.println("Here are all the pages currently in the graph: ");
		printAllGraph();

	}

	public static void hitsCounter(){//controls how many iterations of hits Algorithm are run
		iterat++; //testing
		hitsAlgorithm();
		double currentErr=findMaxError();
		if (currentErr > 0.0001){//if max error is above hard-coded threshold we continue
			updateAuth_Hub();
			hitsCounter();
		}
		else{
			updateAuth_Hub();
		}
	}


	public static void hitsAlgorithm (){//main method to excecute HITS algorithm
		double hNorm =0;
		double aNorm =0;
		for(int i=0; i<graph.size();i++){
			Link curr = graph.get(i);
			List<Link> pointToYou = curr.getLinkTo();

			//update hub value for current link
			for(int j=0; j < pointToYou.size();j++){// target link in LinkTo List
				String target1 = pointToYou.get(j).getUrl();

				for(int y=0;y<graph.size();y++){
					if(target1.equals(graph.get(y).getUrl())){//find this link in the neighborhood graph
						double orginalNHub=graph.get(i).getNextHub();
						double orginalNHub2=orginalNHub+graph.get(y).getAuthority();
						graph.get(i).setNextHub(orginalNHub2);//update hub value for current link


						double orginalNAuth=graph.get(y).getNextAuthority();//update authority values for links that I link to
						double orginalNAuth2=orginalNAuth+graph.get(i).getHub();
						graph.get(y).setNextAuthority(orginalNAuth2);
					}
				}
			}
		}
		for (int l =0;l<graph.size();l++){//calcuate norm variable
			double n=graph.get(l).getNextAuthority();
			aNorm=aNorm+n*n;

			double m = graph.get(l).getNextHub();
			hNorm=hNorm+m*m;

		}
		hNorm=Math.sqrt(hNorm);
		aNorm=Math.sqrt(aNorm);

		addUpdateNorm(hNorm,aNorm);//update hub/auth weights to account for normalization
		updateErrorVal(); //update error values for both weights
	}

	private static void addUpdateNorm(double hNorm, double aNorm){//normalize auth/hub weights
		for(int i=0;i<graph.size();i++){
			graph.get(i).setNextHub(graph.get(i).getNextHub()/hNorm);//normalize hub weights
			graph.get(i).setNextAuthority(graph.get(i).getNextAuthority()/aNorm);//normalize auth weights
		}
	}

	private static void updateAuth_Hub(){//update hub and auth values in prepartion for next iteration of HITS
		for (int i=0;i<graph.size();i++){
			graph.get(i).nextIteration();
		}
	}

	private static void updateErrorVal(){//calculate new error values for auth/hub weights
		for(int i =0; i<graph.size();i++){
			graph.get(i).setEr();
		}
	}

	public static double findMaxError(){ //used to calculate error to help determine if another iteration of HITS is needed
		double maxE=0;
		for (int i =0;i<graph.size();i++){//comb through graph to calculate highest error value
			double aE=graph.get(i).getAuthE();
			double hE=graph.get(i).getHubE();
			if(aE>maxE){
				maxE=aE;
			}
			if(hE>maxE){
				maxE=hE;
			}
		}
		return maxE;
	}

	public static void printAllGraph(){//print out info for all graph web pages
		for (Link e:graph) {
			String url = e.getUrl();
			String description = e.getDescription();
			System.out.println("Web Page Url: "+url);
			System.out.println("Yahoo Search Description: "+description);
			System.out.println();
		}
	}

	public static void printRankedGraph(){
		for (int i=0;i<nRank&&i<graph.size();i++){
			String url = graph.get(i).getUrl();
			Double auth = graph.get(i).getAuthority();
			Double hub = graph.get(i).getHub();
			int rank =i+1;

			System.out.println("Web Page Rank #"+rank+" Url: "+url);
			System.out.println("Hub Weight: "+hub);
			System.out.println("Authority Weight: "+auth);
			System.out.println();
		}
	}

	public static void rankEmAuth(){//sort graph by authority weights
		Collections.sort(graph,Link.getAuthorityComparator().reversed());
	}

	public static void rankEmHub(){//sort graph by hub weights
		Collections.sort(graph,Link.getHubComparator().reversed());
	}

	private static void buildTestGraph(){ //used to build a graph with HW5 links to test HITS part of my program
		Link siteA = new Link("A","hey");//add new link to our graph
		graph.add(siteA);
		Link siteB = new Link("B","hey");//add new link to our graph
		graph.add(siteB);
		Link siteC = new Link("C","hey");//add new link to our graph
		graph.add(siteC);
		Link siteD = new Link("D","hey");//add new link to our graph
		graph.add(siteD);
		Link siteE = new Link("E","hey");//add new link to our graph
		graph.add(siteE);
		Link siteF = new Link("F","hey");//add new link to our graph
		graph.add(siteF);
		Link siteG = new Link("G","hey");//add new link to our
		graph.add(siteG);

		siteA.addLinkTo(siteC);
		siteA.addLinkTo(siteE);
		siteA.addLinkTo(siteF);
		siteA.addLinkTo(siteG);

		siteB.addLinkTo(siteC);
		siteB.addLinkTo(siteF);

		siteC.addLinkTo(siteA);
		siteC.addLinkTo(siteD);
		siteC.addLinkTo(siteF);
		siteC.addLinkTo(siteG);

		siteD.addLinkTo(siteB);
		siteD.addLinkTo(siteC);

		siteE.addLinkTo(siteA);
		siteE.addLinkTo(siteD);
		siteE.addLinkTo(siteG);

		siteF.addLinkTo(siteC);
		siteF.addLinkTo(siteE);
		siteF.addLinkTo(siteG);

		siteG.addLinkTo(siteA);
		siteG.addLinkTo(siteB);
		siteG.addLinkTo(siteC);
		siteG.addLinkTo(siteF);
	}

	public static void buildRoot(String term) throws Exception {//method to perform yahoo search for desired term and find first 30 links
		for(int b=1;graph.size()<30;b+=10) {//do http request/response only for first 30 results which will appear on first 3 pages of yahoo search
			try {
				URL yahoo = new URL("https://search.yahoo.com/search?p="+term+"&b="+b);
				URLConnection yc = yahoo.openConnection();
				BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));

				String inputLine;//used to store html response text

				while ((inputLine = in.readLine()) != null) {//while there is more html response text to read
					int lastIndex = 0;

					while(lastIndex != -1){
						String urlString = "<div class=\"yst result\"><h3 class=\"title\"><a href=\"";
						String descripString = "class=\"abstract ellipsis\">";

						String nLink;
						String nDescription;

						lastIndex = inputLine.indexOf(urlString,lastIndex);

						if(lastIndex != -1){
							lastIndex += urlString.length();
							nLink = inputLine.substring(lastIndex, inputLine.indexOf("\" class", lastIndex));//find url in html response

							boolean check1 = nonGrataCheck(nLink,"gooasddGoogelyMoogely");//make sure link is acceptable
							boolean check2 = redundantLink(nLink);
							if(check1&&check2&&graph.size()<30){
								lastIndex = inputLine.indexOf(descripString,lastIndex);//move cursor to location of description for url
								lastIndex+=descripString.length();
								nDescription = inputLine.substring(lastIndex, inputLine.indexOf("</p>", lastIndex));

								Link site = new Link(nLink,nDescription);//add new link to our graph
								graph.add(site);
							}
						}
					}
				}
				in.close();
			} catch (Exception e){
				continue;
			}

		}

	}

	public static void buildNeighborhood() throws Exception{//build out neighborhood graph
		//array of terms that should not be included the url
		int oneIt=graph.size();
		for (int i =0;i < oneIt;i++){
			String origin = graph.get(i).getUrl();
			//find current domain of website; used for disregarding internal links
			int cind = origin.indexOf(".");
			String domain;

			if(cind <0){//no periods in html name
				domain = origin;
			}
			else{//only one period in html name
				if (origin.indexOf(".",cind+1)<0){
					int slashind = origin.indexOf("//");
					domain= origin.substring(slashind+2,cind-1);
				}
				else{//at least 2 periods in html name
					domain= origin.substring(cind+1,origin.indexOf(".",cind+1));
				}
			}

			try {
				URL yahoo = new URL(origin);
				URLConnection yc = yahoo.openConnection();
				BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));

				String inputLine;//used to store html response text

				while((inputLine = in.readLine()) != null && graph.get(i).getLinkTo().size()< kLimit){
					int lastIndex = 0;

					while(lastIndex != -1){
						String urlString = "a href=\"http";
						String nLink;
						String nDescription;

						lastIndex = inputLine.indexOf(urlString,lastIndex);

						if(lastIndex != -1){
							lastIndex += urlString.length()-4;
							nLink = inputLine.substring(lastIndex, inputLine.indexOf("\"", lastIndex));//find url in html response

							int cind1 = nLink.indexOf(".");
							String domain1;
							if(cind1 <0){//no periods in html name
								domain1 = nLink;
							}
							else{//only one period in html name
								if (nLink.indexOf(".",cind1+1)<0){
									int slashind1 = nLink.indexOf("//");
									domain1= nLink.substring(slashind1+2,cind1-1);
								}
								else{//at least 2 periods in html name
									domain1= nLink.substring(cind1+1,nLink.indexOf(".",cind1+1));//TODO change to avoid errors;
								}
							}

							boolean check1 = redundantLink(nLink);
							boolean check2 = nonGrataCheck(nLink,domain);

							if(check2){
								if(check1){
									nDescription= findDescription(nLink);
									Link site = new Link(nLink,nDescription);//add new link to our graph
									graph.add(site);

									//update current link's data
									graph.get(i).getLinkTo().add(site);//add to linkTo array for this specific link

								}
								else{
									for(int z =0; z<graph.size(); z++){
										if(nLink.equals(graph.get(z).getUrl())){
											graph.get(i).getLinkTo().add(graph.get(z));//add to linkTo array for this specific link
											break;
										}
									}

								}

							}
						}
					}

				}

				in.close();
			} catch (Exception e){//if error is caught, like a bad url, just skip to next website url
				continue;
			}
		}
	}

	public static boolean redundantLink(String nLink){//check if new proposed link is already in our graph
		for (int i =0;i < graph.size();i++) {
			Link cool = graph.get(i);
			if (cool.getUrl().equals(nLink) || cool.getUrl().contains(nLink)|| nLink.contains(cool.getUrl())){
				return false;
			}
		}
		return true;
	}

	public static boolean nonGrataCheck(String nLink, String domain){//check if current link has unwanted terms in it
		String [] nonGrata = {"facebook", "twitter", "linkedin", "youtube", "doubleclick", "ads", ".png", ".jpg", ".svg",
				"Cookie_statement", "Privacy_policy","dell"};

		if(nLink.contains(domain))
			return false;


		for(int y=0;y<nonGrata.length;y++){
			if(nLink.contains(nonGrata[y])){
				return false;
			}
		}
		return true;
	}

	public static String findDescription(String nLink) throws Exception{//find the yahoo description info for a given link
		String descrip="";
		URL yahoo = new URL("https://search.yahoo.com/search?p="+nLink);
		URLConnection yc = yahoo.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));

		String inputLine;//used to store html response text

		while ((inputLine = in.readLine()) != null) {//while there is more html response text to read
			int lastIndex = 0;
			String descripString = "class=\"abstract ellipsis\">";

			lastIndex = inputLine.indexOf(descripString,lastIndex);

			if(lastIndex != -1){
				lastIndex+=descripString.length();
				descrip = inputLine.substring(lastIndex, inputLine.indexOf("</p>", lastIndex));
			}
		}
		in.close();
		return descrip;
	}
}
