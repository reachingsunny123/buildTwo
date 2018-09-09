package com.learn2gether.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.learn2gether.comands.CheckoutCommand;
import com.learn2gether.dao.CustomerRepository;
import com.learn2gether.domain.Address;
import com.learn2gether.domain.Customer;
import com.learn2gether.domain.CustomerImage;
import com.learn2gether.services.FileArchiveService;

import java.util.List;

import javax.validation.Valid;

/**
 * Created by jt on 2/1/16.
 */

@Controller
public class CheckoutController {
	
	@Autowired
	private CustomerRepository customerRepository;
	
	@Autowired
	private FileArchiveService fileArchiveService; 


    @RequestMapping("/checkout")
    public String checkoutForm(Model model){

        model.addAttribute("checkoutCommand", new CheckoutCommand());

        return "checkoutform";
    }

    @RequestMapping(value = "/docheckout", method = RequestMethod.POST)
    public String doCheckout(@Valid CheckoutCommand checkoutCommand, BindingResult bindingResult){

        if (bindingResult.hasErrors()) {
            return "checkoutform";
        }
        CustomerImage customerImage = null;
        if(checkoutCommand.getPhoto()!=null) {
        	try {
        	customerImage = fileArchiveService.saveFileToS3(checkoutCommand.getPhoto());  
        	}catch(Throwable t){
        		t.printStackTrace();
        		System.out.println("Photo cannot be loaded");
        	}
        }
        Customer customer = new Customer(checkoutCommand.getFirstName(), checkoutCommand.getLastName(), null, customerImage, checkoutCommand.getPhone(),checkoutCommand.getEmail(),
        		new Address(null, null, null, null));

        Customer customerNew = customerRepository.save(customer);

        return "checkoutcomplete";

    }
    
    @RequestMapping({"/customer/list"})
    public String listProducts(Model model){
    	List<Customer> list = (List<Customer>) customerRepository.findAll();
    	
        model.addAttribute("customers", list);
        return "customers";
    }


}
