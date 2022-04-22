package com.user.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.user.model.IndicomAirlines;
import com.user.model.Passenger;
import com.user.model.Ticket;
import com.user.repository.AirlinesRepository;
import com.user.repository.PassengerRepository;
import com.user.repository.TicketRepository;

@Service
public class AirlineServiceImpl<E> implements AirlineServiceInterface {

	@Autowired
	private AirlinesRepository airlineRepo;

	@Autowired
	private PassengerRepository prepo;

	@Autowired

	private TicketRepository ticketRepository;
	//1. Passengers should be able to book tickets – show the ticket cost once booking is done.
	@Override
	public IndicomAirlines saveTicket(IndicomAirlines ticket) {

		return airlineRepo.save(ticket);
	}
	//2. Get all the details of  INDI34E.
	@Override
	public List<IndicomAirlines> getFlightPassangers(String flightName) {
		List<IndicomAirlines> list = airlineRepo.findByFlightName(flightName);

		List<IndicomAirlines> l = list.stream().filter(i -> i.getFlightName().contains(flightName))
				.collect(Collectors.toList());

		List<Ticket> tick = new ArrayList();

		for (IndicomAirlines ia : l) {
			tick.addAll(ia.getTickets());
		}
		return list;

	}
	//3. Get all the details of INDI67G
	@Override
	public List<IndicomAirlines> getList(String flightName) {
		List<IndicomAirlines> list1 = airlineRepo.findByFlightName(flightName);

		List<IndicomAirlines> collect = list1.stream().filter(i -> i.getFlightName().contains("INDI67G"))
				.collect(Collectors.toList());
		List<Ticket> t = new ArrayList();

		for (IndicomAirlines collect1 : list1) {
			t.addAll(collect1.getTickets());
		}

		return list1;
	}

	@Override
	public List<IndicomAirlines> fList(IndicomAirlines indicomAirlines) {
		List<IndicomAirlines> list = airlineRepo.findAll();

		return list;
	}
	//4. Get all passenger details of INDI67G sorted by name or age.
	// sprtby age flight
	@Override
	public List<Passenger> sortByAge(String flightname) {

		List<IndicomAirlines> a = airlineRepo.findByFlightName(flightname);
		List<Ticket> ticket = new ArrayList<>();

		for (IndicomAirlines air : a) {
			ticket.addAll(air.getTickets());
		}
		List<Passenger> p = new ArrayList<>();
		for (Ticket tic : ticket) {
			p.addAll(tic.getPassengers());
		}
		Collections.sort(p, new serviceComparator());

		return p;

		// List<Passenger> list2 = prepo.findByage();

		// return list3;
	}

	@Override
	public List<Passenger> servhByTicSortByAge() {
		List<Passenger> list = prepo.searchByTicSort();
		return list;
	}
	//5. Get all passengers in a particular ticket (search with ticket id) sorted by age	
	@Override
	public List<Passenger> tickets(int id) {
		List<Passenger> list = prepo.findBytId(id);

		Collections.sort(list, (o1, o2) -> {
			return o1.getAge() - o2.getAge();
		});

		return list;

	}
	//6. cancel an entire journey – if ticket is cancelled within 1 hour of journey, no refund is given. If cancelled within 5 hours, 50% is refunded.
	@Override
	public String canceltic(int id) {

		// SimpleDateFormat obj = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Ticket t = ticketRepository.getById((long) id);
		Date date1 = t.getBoardingDateAndTime();
		Date date2 = new Date();
		System.out.println("it is date2 " + date2);
		long diffInMillies = date1.getTime() - date2.getTime();
		long days = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
		System.out.println("days" + days);
		int hours = (int) ((diffInMillies / (1000 * 60 * 60)) % 24);
		System.out.println(hours + " hours");
		System.out.println("it is day " + days);
        //Cost printing code
        
        List<Ticket> t1=new ArrayList<>();
        Ticket tick=ticketRepository.getById((long) id);
        int refund=tick.getCost();
        List<Passenger> p2=tick.getPassengers();
        int totalPassenger=p2.size();
        System.out.println("totalpass"+ totalPassenger);
        
        refund=refund*totalPassenger;
        System.out.println("refund .."+refund);
		
		if (days >= 0 && hours >= 0) {

			if (hours <= 1 && days == 0) {
				//ticketRepository.deleteById((long) id);
				return "No refund allowed";
			} else if ((hours <= 5 && hours > 1) && (days == 0 && hours <= 5)) {
				//ticketRepository.deleteById((long) id);
				return "50% refund allowed:"+refund/2;
			} else {
				//ticketRepository.deleteById((long) id);
				return "full refund allowed:"+refund;
			}
		} else {
			return "unable to cancel the ticket..your flight is already departed";
		}
		
	}
	//7. cancel partial ticket – no refund allowed.
		@Override
	public String deletebyId(int tId, Long pId) {

		List<Passenger> passengers = prepo.findAll();
		// System.out.println(passengers.size());
		if (passengers != null) {
			for (int i = 0; i <= passengers.size() - 1; i++) {
				// System.out.println(i);

				if ((passengers.get(i).getId() == pId) && (passengers.get(i).gettId() == tId)) {
					String name = passengers.get(i).getName();
					prepo.delete(passengers.remove(i));

					return "No refund allowed to::" + name;

				}

			}

		}

		return "No passanger avilable on ticket::" + tId;
	}
		
		
		@Override
		public void deleteByPassengers(Long id) {
			
			 prepo.deleteById(id);
		}

}
