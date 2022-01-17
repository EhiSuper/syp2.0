import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Location } from '@angular/common';

import { User } from '../../../interfaces/user';
import { UserService } from '../../../services/user.service';

@Component({
  selector: 'app-user-detail',
  templateUrl: './user-detail.component.html',
  styleUrls: ['./user-detail.component.css']
})
export class UserDetailComponent implements OnInit {
  user: User | undefined;
  snapshot: User | undefined
  show: string | undefined
  userLoggedIn: User | undefined
  followed: boolean | undefined
  myAccount: boolean | undefined
  allowed: boolean | undefined
  showModifyForm: boolean | undefined = false

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private userService: UserService,
    private location: Location
  ) { }

  ngOnInit(): void {
    this.getUserLoggedIn()
    this.route.paramMap.subscribe(params => {
      var id = this.route.snapshot.paramMap.get("id")!;
      this.getUser(id)
    });
  }

  //function to check if the userlogged in is allowed to do some operations
  checkAllowed(): void{
    if(this.userLoggedIn?.isAdmin == true){
      this.allowed = true
      return
    }
    else{
      if(this.userLoggedIn?.username == this.user?.username){
        this.allowed = true
        return
      }
      this.allowed = false
    }
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

  //function to get the followed of the user logged in
  getUserLoggedInFollowed(): void{
    this.userService.getFollowed(this.userLoggedIn!.id)
      .subscribe(followed => {
        this.userLoggedIn!.followed = followed
        this.updateUserLoggedIn()
        this.checkFollowed()
      })
  }

  //function to check if the user logged in already follows the current user
  checkFollowed(): void {
    if(!this.userLoggedIn?.followed){
      this.followed = false
      return
    }
    for (var i = 0; i < this.userLoggedIn!.followed!.length; i++) {
      if (this.userLoggedIn?.followed![i].username == this.user?.username) {
        this.followed = true
        return
      }
    }
    this.followed = false
  }

  //function to update the user logged in
  updateUserLoggedIn(): void {
    localStorage.removeItem("userLoggedIn")
    const jsonData = JSON.stringify(this.userLoggedIn);
    localStorage.setItem('userLoggedIn', jsonData);
    this.getUserLoggedIn()
  }

  //function that add to the current user the user logged in as follower 
  follow(): void {
    if(!this.userLoggedIn?.followed){
      this.userLoggedIn!.followed = []
    }
    this.userService.follow(this.userLoggedIn!.id, this.user!.id).subscribe(
      {
        next: () => {
          this.userLoggedIn?.followed!.push(this.user!)
          this.updateUserLoggedIn()
          this.followed = true
        },
        error: () => {
          window.alert("operation failed")
        }}
    )
  }

  findIndex(): number{
    for(var i=0; i<this.userLoggedIn!.followed!.length; i++){
      if(this.userLoggedIn?.followed![i].id == this.user?.id) return i
    }
    return -1
  }

  unfollow(): void{
    this.userService.unfollow(this.userLoggedIn!.id, this.user!.id).subscribe(
      {
        next: () => {
          var index = this.findIndex()
          this.userLoggedIn?.followed?.splice(index, 1)
          this.updateUserLoggedIn()
          this.followed = false
        },
        error: () => {
          window.alert("operation failed")
        }}
    )
  }

  //function to check the user logged in is in it's own page
  isMyAccount(): void{
    if(this.userLoggedIn?.username == this.user?.username) this.myAccount = true
    else this.myAccount = false
  }

  //function to get the current user
  getUser(id: string): void {
    this.userService.getUser(id)
      .subscribe(user => {
        this.user = user
        this.show = 'playlistsCreated'
        this.checkAllowed()
        this.isMyAccount()
        if(!this.myAccount){
          if (!this.userLoggedIn) return
          if(this.userLoggedIn.followed == undefined) this.getUserLoggedInFollowed()
          else this.checkFollowed()
        }
      });
  }

  goBack(): void {
    this.location.back();
  }

  modifyUser(){
    this.showForm()
    this.snapshot = Object.assign({}, this.user)
  }

  //function to save the user, called by the form too modify the user
  saveUser(): void {
    if (this.user) {
      this.userService.updateUser(this.snapshot!, this.user)
        .subscribe({
          next: () => {
            if(this.myAccount){
              this.user!.followed = this.userLoggedIn?.followed
              this.userLoggedIn = this.user
              this.updateUserLoggedIn()
            }
          },
          error: () => {
            this.user = Object.assign({}, this.snapshot)
            window.alert("operation failed")
          }});
    }
    this.showForm()
  }

  showForm(): void{
    this.showModifyForm = !this.showModifyForm
  }

  setShow(show: string): void {
    this.show = show;
  }

  //function to delete the current userd
  deleteUser(): void {
    this.userService.deleteUser(this.user!.id);
    if(this.myAccount){
      this.userLoggedIn = undefined
      localStorage.removeItem("userLoggedIn")
    }
    this.router.navigateByUrl('/dashboard')
  }
}
