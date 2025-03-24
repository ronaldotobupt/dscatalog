package com.ronaldosantos.dscatalog.services;



import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import com.ronaldosantos.dscatalog.dto.RoleDTO;
import com.ronaldosantos.dscatalog.dto.UserDTO;
import com.ronaldosantos.dscatalog.dto.UserInsertDTO;
import com.ronaldosantos.dscatalog.dto.UserUpdateDTO;
import com.ronaldosantos.dscatalog.entities.Role;
import com.ronaldosantos.dscatalog.entities.User;
import com.ronaldosantos.dscatalog.projections.UserDetailsProjection;
import com.ronaldosantos.dscatalog.repositories.RoleRepository;
import com.ronaldosantos.dscatalog.repositories.UserRepository;
import com.ronaldosantos.dscatalog.services.exceptions.DatabaseException;
import com.ronaldosantos.dscatalog.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;



@Service
public class UserService implements UserDetailsService {
	
	@Autowired
	private UserRepository repository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoderUser;
	
	@Transactional(readOnly = true)
	public Page<UserDTO> findAllPaged(Pageable pageable){
		Page<User> list = repository.findAll(pageable);
		return list.map(x -> new UserDTO(x));
	}
	
	@Transactional(readOnly = true)
	public UserDTO findById(@PathVariable Long id) {
		User dto = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));
		return new UserDTO(dto);
		
	}

	@Transactional
	public UserDTO insert(UserInsertDTO dto) {
		User entity = new User();
		copyDtoToEntity(dto, entity);
		
		entity.getRoles().clear();
		Role role = roleRepository.findByAuthority("ROLE_OPERATOR");
		entity.addRole(role);
		
		entity.setPassword(passwordEncoderUser.encode(dto.getPassword()));
		
		entity = repository.save(entity);
		return new UserDTO(entity);
	}

	@Transactional
	public UserDTO update(Long id,UserUpdateDTO dto) {
		try {
		User entity = repository.getReferenceById(id);
		copyDtoToEntity(dto, entity);
		entity = repository.save(entity);
		return new UserDTO(entity);
		}
		catch(EntityNotFoundException e) {
			throw new ResourceNotFoundException("Id não encontrado");
		}
	}

	@Transactional(propagation = Propagation.SUPPORTS)
	public void delete(Long id) {
		if (!repository.existsById(id)) {
			throw new ResourceNotFoundException("Database error");
		}
		try {
	        	repository.deleteById(id);    		
		}
	    	catch (DataIntegrityViolationException e) {
	        	throw new DatabaseException("Falha de integridade referencial");
	   	}
	}
	
	private void copyDtoToEntity(UserDTO dto, User entity) {
		entity.setFirstName(dto.getFirstName());
		entity.setLastName(dto.getLastName());
		entity.setEmail(dto.getEmail());
				
		entity.getRoles().clear();
		for(RoleDTO rolDto : dto.getRoles()) {
			Role role = roleRepository.getReferenceById(rolDto.getId());
			entity.getRoles().add(role);
		}
		
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		List<UserDetailsProjection> list = repository.searchUserAndRolesByEmail(username);
		
		if(list.size() == 0) {
			throw new UsernameNotFoundException("User not found");
		}
		
		User user = new User();
		user.setEmail(username);
		user.setPassword(list.get(0).getPassword());
		for(UserDetailsProjection projection : list) {
			user.addRole(new Role(projection.getRoleId(), projection.getAuthority()));
		}
		
		return user;
	}

}
