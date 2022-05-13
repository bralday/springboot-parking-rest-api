package com.springboot.parking.service.impl;


import com.springboot.parking.entity.Slot;
import com.springboot.parking.entity.Ticket;
import com.springboot.parking.entity.Vehicle;
import com.springboot.parking.enums.SlotSize;
import com.springboot.parking.exception.AvailableSlotNotFoundException;
import com.springboot.parking.exception.ResourceNotFoundException;
import com.springboot.parking.payload.TicketDto;
import com.springboot.parking.payload.VehicleDto;
import com.springboot.parking.repository.SlotRepository;
import com.springboot.parking.repository.TicketRepository;
import com.springboot.parking.repository.VehicleRepository;
import com.springboot.parking.service.TicketService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class TicketServiceImpl implements TicketService {

    private TicketRepository ticketRepository;
    private SlotRepository slotRepository;
    private VehicleRepository vehicleRepository;
    private ModelMapper mapper;

    public TicketServiceImpl(TicketRepository ticketRepository, SlotRepository slotRepository, VehicleRepository vehicleRepository, ModelMapper mapper) {
        this.ticketRepository = ticketRepository;
        this.slotRepository = slotRepository;
        this.vehicleRepository = vehicleRepository;
        this.mapper = mapper;
    }

    @Override
    public TicketDto park(VehicleDto vehicleDto){
        Vehicle newVehicle = mapToVehicleEntity(vehicleDto); // New Vehicle transaction
        Vehicle vehicle;
        boolean isReturnee = false;
        // Check if vehicle is registered in db, if not save.
        if(vehicleRepository.existsByLicenseNumber(newVehicle.getLicenseNumber())){
            vehicle = vehicleRepository.findByLicenseNumber(vehicleDto.getLicenseNumber());

            // Check if tickets with vehicle exist, list all tickets and get the latest one.
            if (ticketRepository.existsByLicenseNumber(vehicle.getLicenseNumber())){
                List<Ticket> tickets = ticketRepository.findByLicenseNumber(vehicle.getLicenseNumber());
                Ticket latestTicket = tickets.get(tickets.size()-1);
                latestTicket.setEntryPoint(newVehicle.getEntryPoint()); // Set new entry point of the vehicle

                // Check if the latest one is not yet pass an hour.
                if(checkIfRecent(latestTicket.getTimeOut())){
                    isReturnee = true;
                    return generateTicket(latestTicket,vehicle, isReturnee);
                }
            }
        }else{
            vehicle = this.vehicleRepository.save(newVehicle);
        }




        // if not qualified as returnee, generate a new ticket
        Ticket newTicket = new Ticket();
        newTicket.setEntryPoint(newVehicle.getEntryPoint()); //entry point for new transaction
        newTicket.setVehicleSize(vehicle.getVehicleSize());
        newTicket.setLicenseNumber(vehicle.getLicenseNumber());


        return generateTicket(newTicket,vehicle, isReturnee);

    }

    @Override
    public TicketDto unpark(long ticketId) {

        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(
                () -> new ResourceNotFoundException("Ticket", "id", ticketId));
        Slot slot = slotRepository.findById(ticket.getSlot().getId()).orElseThrow(
                () -> new ResourceNotFoundException("Slot", "id", ticket.getSlot().getId()));
        LocalDateTime currentTime = LocalDateTime.now();
        BigDecimal recentDue = ticket.getAmountDue();

        System.out.println("Recent Due: " + recentDue);

        // check if 3 hours or less
        if (checkIfMinimum(ticket.getTimeIn(),currentTime)) {

            ticket.setTimeOut(currentTime);
            ticket.setAmountDue(new BigDecimal(slot.getFlatRate().longValue() - recentDue.longValue()));
            System.out.println("Minimum Amount Due " + new BigDecimal(slot.getFlatRate().longValue() - recentDue.longValue()));

            slot.setOccupied(false);
            this.slotRepository.save(slot);

            Ticket updatedTicket = ticketRepository.save(ticket);

            return mapToDto(ticket);
        }

        // check if more than 24 hours
        if (checkIfWholeDay(ticket.getTimeIn(),currentTime)){
            ticket.setTimeOut(currentTime);
            ticket.setAmountDue(computeDaily(ticket.getTimeIn(),currentTime,slot.getPerHour(), recentDue));
            System.out.println("Daily Amount Due " + computeDaily(ticket.getTimeIn(),currentTime,slot.getPerHour(), recentDue));

            slot.setOccupied(false);
            this.slotRepository.save(slot);

            Ticket updatedTicket = ticketRepository.save(ticket);

            return mapToDto(ticket);
        }


        // if conditions above are not satisfied
        ticket.setTimeOut(currentTime);
        ticket.setAmountDue(computeHourly(ticket.getTimeIn(),currentTime,slot.getFlatRate(),slot.getPerHour(), recentDue));
        System.out.println("Hourly Amount Due " + computeHourly(ticket.getTimeIn(),currentTime,slot.getFlatRate(),slot.getPerHour(), recentDue));
        slot.setOccupied(false);
        this.slotRepository.save(slot);

        Ticket updatedTicket = ticketRepository.save(ticket);

        return mapToDto(ticket);

    }

    @Override
    public List<TicketDto> getAllTickets(long parkingLotId) {
        List<Ticket> tickets = ticketRepository.findByParkingLotId(parkingLotId);

        return tickets.stream().map(ticket -> mapToDto(ticket)).collect(Collectors.toList());
    }

    // Convert DTO to entity
    private Ticket mapToEntity (TicketDto ticketDto){

        Ticket ticket = mapper.map(ticketDto, Ticket.class);

        return ticket;
    }

    private VehicleDto mapToVehicleDto(Vehicle vehicle){

        VehicleDto vehicleDto = mapper.map(vehicle, VehicleDto.class);

        return vehicleDto;
    }

    // Convert DTO to entity
    private Vehicle mapToVehicleEntity (VehicleDto vehicleDto){

        Vehicle vehicle = mapper.map(vehicleDto, Vehicle.class);

        return vehicle;
    }

    //convert entity (post) to DTO
    private TicketDto mapToDto(Ticket ticket){

        TicketDto ticketDto = mapper.map(ticket, TicketDto.class);

        return ticketDto;
    }

    private TicketDto generateTicket(Ticket ticket, Vehicle vehicle, boolean isReturnee){
        // find all slots within entry point and size
        System.out.println("Properties: " + ticket.getEntryPoint() + " " +ticket.getVehicleSize());
        List<Slot> nearSlots = slotRepository.findByEntryPointAndSlotSize(ticket.getEntryPoint(), ticket.getVehicleSize());
        System.out.println("here1");

        for(Slot slot : nearSlots){

            if (slot.getSlotSize().toString().equals(ticket.getVehicleSize()) && !slot.isOccupied()){


                // Add additional ticket details
                // If not a 1 hour returnee, set time in as now.
                if(!isReturnee){
                    ticket.setTimeIn(LocalDateTime.now());
                    ticket.setAmountDue(new BigDecimal(0.00));
                }
                ticket.setSlot(slot);
                ticket.setParkingLot(slot.getParkingLot());
                ticket.setVehicle(vehicle);


                // Save ticket to db
                Ticket updatedTicket = this.ticketRepository.save(ticket);

                slot.setOccupied(true);
                slot.setOccupant(vehicle.getLicenseNumber());
                this.slotRepository.save(slot);
                return mapToDto(updatedTicket);

            }else{
                continue;
            }
        }

        /* If enhanced for loop ends and still none, check all other slots regardless of entry point
         * Also check for slots where it can fit
         */
        List<Slot> otherSlots = slotRepository.findAll(); //Fixed to find ALL slots
        for(Slot slot : otherSlots){
            System.out.println("Ticket Properties: " + ticket.getEntryPoint() + " " +ticket.getVehicleSize());
            System.out.println("Other slot property: " + slot.getEntryPoint() + " " + slot.getSlotSize() + " " + slot.isOccupied());
            System.out.println("===========================================");
            if (((slot.getSlotSize().equals(ticket.getVehicleSize()) && !slot.isOccupied())
                    || (ticket.getVehicleSize().equals(SlotSize.SP.name()) && slot.getSlotSize().equals(SlotSize.MP.name()) && !slot.isOccupied())
                    || (ticket.getVehicleSize().equals(SlotSize.SP.name()) && slot.getSlotSize().equals(SlotSize.LP.name())&& !slot.isOccupied())
                    || (ticket.getVehicleSize().equals(SlotSize.MP.name()) && slot.getSlotSize().equals(SlotSize.LP.name()))&& !slot.isOccupied())
                    ){



                // Add additional ticket details
                // If not a 1 hour returnee, set time in as now.
                if(!isReturnee){
                    ticket.setTimeIn(LocalDateTime.now());
                }
                ticket.setEntryPoint(slot.getEntryPoint()); //set new entry point of the slot
                ticket.setParkingLot(slot.getParkingLot());
                ticket.setSlot(slot);
                ticket.setVehicle(vehicle);

                // Save ticket to db
                Ticket updatedTicket = this.ticketRepository.save(ticket);

                slot.setOccupied(true);
                slot.setOccupant(vehicle.getLicenseNumber());
                this.slotRepository.save(slot);
                return mapToDto(updatedTicket);

            }else{
                continue;
            }
        }

        throw new AvailableSlotNotFoundException();
    }

    // Check if a vehicle leaving the parking complex returned within one hour
    private boolean checkIfRecent(LocalDateTime exitDate){
        LocalDateTime timeLimit = exitDate.plusHours(1);

            if(LocalDateTime.now().isBefore(timeLimit) || LocalDateTime.now().isEqual(timeLimit)) {
                return true;
            }
        return false;
    }

    // Check if within 3 hours
    private boolean checkIfMinimum(LocalDateTime entryDate, LocalDateTime exitDate){
        LocalDateTime timeLimit = entryDate.plusHours(3);
        if(exitDate.isBefore(timeLimit) || exitDate.isEqual(timeLimit)){
            return true;
        }
        return false;
    }

    // Check if within 24 hours
    private boolean checkIfWholeDay(LocalDateTime entryDate, LocalDateTime exitDate){
        LocalDateTime timeLimit = entryDate.plusDays(1);
        if(exitDate.isAfter(timeLimit) || exitDate.isEqual(timeLimit)){
            return true;
        }
        return false;
    }

    // Compute for 24 hour
    private BigDecimal computeDaily(LocalDateTime entryDate, LocalDateTime exitDate, BigDecimal rate, BigDecimal recentDue){
        long amount_due;
        long flat_daily = 5000;

        long days = ChronoUnit.DAYS.between(entryDate, exitDate);
        long hours = ChronoUnit.HOURS.between(entryDate,exitDate)%(days*24);

        amount_due = (flat_daily * days) + (hours * rate.longValue())  - recentDue.longValue();

        return new BigDecimal(amount_due);
    }

    // Compute for hourly rate
    private BigDecimal computeHourly(LocalDateTime entryDate, LocalDateTime exitDate, BigDecimal flat_rate, BigDecimal per_hour, BigDecimal recentDue){
        long amount_due;
        long hours = ChronoUnit.HOURS.between(entryDate,exitDate)-3;

        amount_due = (hours * per_hour.longValue()) + flat_rate.longValue() - recentDue.longValue();
        System.out.println(recentDue);
        return new BigDecimal(amount_due);
    }

}
