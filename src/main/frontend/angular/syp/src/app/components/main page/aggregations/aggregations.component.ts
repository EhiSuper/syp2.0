import { Component, OnInit } from '@angular/core';
import { Aggregation } from '../../../interfaces/aggregation';
import { User } from '../../../interfaces/user';

@Component({
  selector: 'app-aggregations',
  templateUrl: './aggregations.component.html',
  styleUrls: ['./aggregations.component.css']
})
export class AggregationsComponent implements OnInit {

  aggregations: Aggregation[] = [
    {
      name: "How many songs has a playlist in average?",
      endpoint: "/api/playlists/averagesongs",
      access: "admin",
      resultType: "number"
    },
    {
      name: "How many followers has a playlist in average?",
      endpoint: "/api/playlists/averagefollows",
      access: "admin",
      resultType: "number"
    },
    {
      name: "How many comments has a song in average?",
      endpoint: "/api/songs/averagecomments",
      access: "admin",
      resultType: "number"
    },
    {
      name: "On average a song in how many playlists is contained?",
      endpoint: "/api/songs/averageplaylists",
      access: "admin",
      resultType: "number"
    },
    {
      name: "How many followers has a user in average?",
      endpoint: "/api/users/averagefollows",
      access: "admin",
      resultType: "number"
    },
    {
      name: "How many playlists are followed by a user in average?",
      endpoint: "/api/users/averageplaylists",
      access: "admin",
      resultType: "number"
    },
    {
      name: "How many songs a user comments in average?",
      endpoint: "/api/users/averagecomments",
      access: "admin",
      resultType: "number"
    },
    {
      name: "How many playlists a user creates in average?",
      endpoint: "/api/users/averageplaylists",
      access: "admin",
      resultType: "number"
    },
    {
      name: "Find the top k users that has created the highest number of playlists",
      endpoint: "/api/users/topcreators",
      parameters: ["number"],
      access: "admin",
      resultType: "users"
    },
    {
      name: "Find the top k users that have added to them playlists the highest number of songs of a specific artist",
      endpoint: "/api/users/mostsongsofartist",
      parameters: ["number", "artist"],
      access: "user",
      resultType: "users"
    },
    {
      name: "Find the k most popular songs (based on how many playlists contains that specific song)",
      endpoint: "/api/songs/popular",
      parameters: ["number"],
      access: "user",
      resultType: "songs"
    },
    {
      name: "Find the k most followed users",
      endpoint: "/api/users/mostfollowed",
      parameters: ["number"],
      access: "user",
      resultType: "users"
    },
    {
      name: "Find the k most followed playlists",
      endpoint: "/api/playlists/mostfollowed",
      parameters: ["number"],
      access: "user",
      resultType: "playlists"
    },
    {
      name: "Find the users that follows at least k same playlists of the User provided in input",
      endpoint: "/api/users/similar",
      parameters: ["playlists", "username"],
      access: "user",
      resultType: "users"
    },
    {
      name: "Find the k songs that has the highest number of comments",
      endpoint: "/api/songs/mostcommented",
      parameters: ["number"],
      access: "admin",
      resultType: "songs"
    },
    {
      name: "Find the playlists followed by users that a specific user follows",
      endpoint: "/api/playlists/suggested",
      parameters: ["number", "username"],
      access: "user",
      resultType: "playlists"
    }
  ]

  userLoggedIn: User | undefined

  constructor() { }

  ngOnInit(): void {
    this.getUserLoggedIn()
  }

  //function to get the user logged in
  getUserLoggedIn(): void {
    var user = localStorage.getItem("userLoggedIn")
    if (user == null) {
      this.userLoggedIn = undefined
      return
    }
    this.userLoggedIn = JSON.parse(user)
  }

}
