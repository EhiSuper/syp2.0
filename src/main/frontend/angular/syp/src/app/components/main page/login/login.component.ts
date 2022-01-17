import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { User } from 'src/app/interfaces/user';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  user: User | undefined
  username: string | undefined
  password: string | undefined

  constructor(private userService: UserService, private router: Router) { }

  ngOnInit(): void {
  }

  //function to login the user
  login(): void {
    this.userService.getUserByUsername(this.username!)
      .subscribe((user) => {
        this.setUser(user[0])
      });
  }

  //function to set the logged user in the local storage
  setUser(user: User): void {
    if(!user){
      window.alert("Credentials are wrong")
      return
    }
    this.user = user
    if (this.user?.password == this.password) {
      const jsonData = JSON.stringify(this.user);
      localStorage.setItem('userLoggedIn', jsonData);
      this.router.navigateByUrl('/dashboard');
    }
    else{
      window.alert("Credentials are wrong")
    }
  }
}
