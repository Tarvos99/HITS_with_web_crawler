/*Jason Grant
HITS Algorithm Project*/
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Link {
	public Link (String url, String description) {
		this.url = url;
		this.description=description;
	}

	private String url;
	private String description;
	private List<Link> linkTo= new ArrayList<Link>(); //list of links that I point to
	private double authority=1.0;//current authority and hub weights
	private double hub =1.0;
	private double nextAuthority=0.0;//used to store new authority value to be used in next iteration of loop
	private double nextHub=0.0;
	private double hubE =0; //error values for hub and auth
	private double authE=0;

	public String getUrl(){
		return url;
	}

	public String getDescription() {
		return description;
	}

	public void addLinkTo(Link neigh){
		linkTo.add(neigh);
	}

	public void setNextAuthority(double x){
		nextAuthority = x;
	}

	public void setNextHub(double x){
		nextHub = x;
	}

	public double getAuthority(){
		return authority;
	}

	public double getHub(){
		return hub;
	}

	public double getNextAuthority(){
		return nextAuthority;
	}

	public double getNextHub(){
		return nextHub;
	}

	public double getHubE(){return hubE;}

	public double getAuthE(){return authE;}

	public List<Link> getLinkTo(){
		return linkTo;
	}

	public String allMyLinks(){//used for testing
		List<String> myLinks = new ArrayList<>();
		for(int i =0; i< linkTo.size();i++){
			myLinks.add(linkTo.get(i).getUrl());
		}
		return myLinks.toString();
	}

	public void nextIteration(){//update auth/hub values in prep for next iteration of HITS algorithm
		authority = nextAuthority;
		hub = nextHub;
		nextAuthority =0;
		nextHub =0;
	}

	public void setEr(){//calculate new error values for this link
		authE=Math.abs(nextAuthority-authority);
		hubE=Math.abs(nextHub-hub);
	}

    static Comparator<Link> getAuthorityComparator() {//comparator to help sort list of links by Authority
        return new Comparator<Link>() {
            @Override
            public int compare(Link o1, Link o2) {
                return (Double.compare(o1.getAuthority(), o2.getAuthority()));
            }
            // compare using attribute 1
        };
    }

    static Comparator<Link> getHubComparator() {//comparator to help sort list of links by Hub
        return new Comparator<Link>() {
            @Override
            public int compare(Link o1, Link o2) {
                return (Double.compare(o1.getHub(), o2.getHub()));
            }
        };
        // compare using attribute 2
    }

}
