package com.bus_ticket.entities;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.bus_ticket.entities.Role;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class User implements UserDetails{

	 @Id
	    @GeneratedValue(strategy = GenerationType.AUTO)
	    private int id;

	    @Column(name = "name", nullable = false, length = 100)
	    private String name;

	    @Column(unique = true)
	    private String email;
       
	    @Column(nullable=false,length=100)
	    private String mobileNo;
	    
	    private String password;

	    private String imageName;
	    
        private String otp;
        //--------------------------------
        
        @Column(name = "date_of_registration")
        private LocalDateTime dateOfRegistration;

        private LocalDateTime date_Of_Role_Changed;
        
        private LocalDateTime otpValidUntil;
        
	    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
		@JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "role", referencedColumnName = "id"))
		private Set<Role> roles = new HashSet<>();
	    
		@Override
		public Collection<? extends GrantedAuthority> getAuthorities() {

			List<SimpleGrantedAuthority> authories = this.roles.stream()
					.map((role) -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
			return authories;
		}

		@Override
		public String getUsername() {
			// TODO Auto-generated method stub
			return this.mobileNo;
		}

		@Override
		public boolean isAccountNonExpired() {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public boolean isAccountNonLocked() {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public boolean isCredentialsNonExpired() {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public boolean isEnabled() {
			// TODO Auto-generated method stub
			return true;
		}

		
	
}
