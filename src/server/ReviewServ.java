package server;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.http.HTTPException;

import models.SQLCalls;


@WebServlet("/ReviewServ")
public class ReviewServ extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public ReviewServ() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//Need to map the review to a location and a User
		String locationID = "1";//request.getParameter("locationID");
		String userID = "1";//request.getParameter("userID");  //maybe getAttribute()?? since user won't be changing often
														 //so maybe we can session.setAttribute("userID", userID) 

		// Connect to MySQL database
		SQLCalls ReviewCalls = new SQLCalls();
		String reviewTitle = "";
		String reviewBody = "";
		if(locationID != null && userID != null){
			reviewTitle = ReviewCalls.reviewToTitle(locationID, userID);
			reviewBody = ReviewCalls.reviewToBody(locationID, userID);
			int upvote = ReviewCalls.reviewToUpvote(locationID);
			int downvote = ReviewCalls.reviewToDownvote(locationID);
			// Output error to user that they can only leave one review per location....
			
			//TEST OUTPUT
			System.out.println(reviewTitle);
			System.out.println(reviewBody);
			System.out.println(upvote);
			System.out.println(downvote);
		}
		if(reviewTitle.isEmpty() && reviewBody.isEmpty()){
			// add new review to the SQL database if user didnt already leave one --- how does front end want to name fields?
			String newtitle = request.getParameter("title");
			String newbody = request.getParameter("body");
			ReviewCalls.addReview(locationID, userID, newtitle, newbody);
		}
		
		
		//want to allow upvote and downvote per review
		String requestType = request.getParameter("requestType");
		//if upvote... increase the upvote count in database, dependent upon whether or not user has already upvoted before
		//front end - upvote and downvote buttons are named 'upvote' and 'downvote'
		if (requestType.contentEquals("upvote")) {
			int currupvote = ReviewCalls.reviewToUpvote(locationID);
			currupvote++;
			ReviewCalls.addUpvote(locationID, currupvote);
		}
		//if downvote, increase the downvote count in database, dependent upon whether or not user has already downvoted before
		if (requestType.contentEquals("downvote")) {
			int currdownvote = ReviewCalls.reviewToUpvote(locationID);
			currdownvote--;
			ReviewCalls.addDownvote(locationID, currdownvote);
		}
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}