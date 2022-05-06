package com.springboot.parking.service.impl;


import com.springboot.parking.entity.Slot;
import com.springboot.parking.entity.Ticket;
import com.springboot.parking.enums.SlotSize;
import com.springboot.parking.exception.AvailableSlotNotFoundException;
import com.springboot.parking.exception.ResourceNotFoundException;
import com.springboot.parking.payload.TicketDto;
import com.springboot.parking.repository.SlotRepository;
import com.springboot.parking.repository.TicketRepository;
import com.springboot.parking.service.TicketService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;


@Service
public class TicketServiceImpl implements TicketService {

    private TicketRepository ticketRepository;
    private SlotRepository slotRepository;
    private ModelMapper mapper;

    public TicketServiceImpl(TicketRepository ticketRepository, SlotRepository slotRepository, ModelMapper mapper) {
        this.ticketRepository = ticketRepository;
        this.slotRepository = slotRepository;
        this.mapper = mapper;
    }

    @Override
    public TicketDto park(TicketDto ticketDto) {

        //TODO check if returnee

        Ticket ticket = mapToEntity(ticketDto);
        System.out.println("Entry point: " + ticket.getEntryPoint());
        System.out.println("Size: " + ticket.getVehicleSize());

        // find all slots within entry point and size
        List<Slot> nearSlots = slotRepository.findByEntryPointAndSlotSize(ticket.getEntryPoint(), ticket.getVehicleSize());
                for(Slot slot : nearSlots){

                    if (slot.getSlotSize().toString().equals(ticket.getVehicleSize()) && !slot.isOccupied()){
                        slot.setOccupied(true);
                        this.slotRepository.save(slot);

                        // Add additional ticket details
                        ticket.setTimeIn(LocalDateTime.now());
                        ticket.setSlot(slot);

                        // Save ticket to db
                        Ticket updatedTicket = this.ticketRepository.save(ticket);
                        return mapToDto(updatedTicket);

                    }else{
                        continue;
                    }
                }

        /* If enhanced for loop ends and still none, check all other slots regardless of entry point
         * Also check for slots where it can fit
         */
        List<Slot> otherSlots = slotRepository.findBySlotSize(ticket.getVehicleSize());
        for(Slot slot : otherSlots){
            System.out.println("Other Slot:");
            System.out.println("SlotSize: " + slot.getSlotSize());
            System.out.println("Entry Point: " + slot.getEntryPoint());
            System.out.println("Occupied: " + slot.isOccupied());
            if (((slot.getSlotSize().equals(ticket.getVehicleSize()))
                    || (ticket.getVehicleSize().equals(SlotSize.SP.name()) && slot.getSlotSize().equals(SlotSize.MP.name()))
                    || (ticket.getVehicleSize().equals(SlotSize.SP.name()) && slot.getSlotSize().equals(SlotSize.LP.name()))
                    || (ticket.getVehicleSize()==SlotSize.MP.name() && slot.getSlotSize().equals(SlotSize.LP.name())))
                    && !slot.isOccupied()){

                slot.setOccupied(true);
                this.slotRepository.save(slot);

                // Add additional ticket details
                ticket.setTimeIn(LocalDateTime.now());
                ticket.setEntryPoint(slot.getEntryPoint());
                ticket.setSlot(slot);

                // Save ticket to db
                Ticket updatedTicket = this.ticketRepository.save(ticket);
                return mapToDto(updatedTicket);

            }else{
                continue;
            }
        }

        throw new AvailableSlotNotFoundException();
    }

    @Override
    public TicketDto unpark(long ticketId) {

        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(
                () -> new ResourceNotFoundException("Ticket", "id", ticketId));
        Slot slot = slotRepository.findById(ticket.getSlot().getId()).orElseThrow(
                () -> new ResourceNotFoundException("Slot", "id", ticket.getSlot().getId()));
        LocalDateTime currentTime = LocalDateTime.now();

        // check if 3 hours or less
        if (checkIfMinimum(ticket.getTimeIn(),currentTime)) {
            ticket.setTimeOut(currentTime);
            ticket.setAmountDue(slot.getFlatRate());

            slot.setOccupied(false);
            this.slotRepository.save(slot);

            Ticket updatedTicket = ticketRepository.save(ticket);

            return mapToDto(ticket);
        }

        // check if more than 24 hours
        if (checkIfWholeDay(ticket.getTimeIn(),currentTime)){
            ticket.setTimeOut(currentTime);
            ticket.setAmountDue(computeDaily(ticket.getTimeIn(),currentTime,slot.getPerHour()));

            slot.setOccupied(false);
            this.slotRepository.save(slot);

            Ticket updatedTicket = ticketRepository.save(ticket);

            return mapToDto(ticket);
        }


        // default if conditions above are not satisfied
        ticket.setTimeOut(currentTime);
        ticket.setAmountDue(computeHourly(ticket.getTimeIn(),currentTime,slot.getFlatRate(),slot.getPerHour()));

        slot.setOccupied(false);
        this.slotRepository.save(slot);

        Ticket updatedTicket = ticketRepository.save(ticket);

        return mapToDto(ticket);

    }

    // Convert DTO to entity
    private Ticket mapToEntity (TicketDto ticketDto){

        Ticket ticket = mapper.map(ticketDto, Ticket.class);

        return ticket;
    }

    //convert entity (post) to DTO
    private TicketDto mapToDto(Ticket ticket){

        TicketDto ticketDto = mapper.map(ticket, TicketDto.class);

        return ticketDto;
    }

    // Check if a vehicle leaving the parking complex returned within one hour
    private boolean checkIfRecent(TicketDto ticketDto){
        Boolean isExisting = ticketRepository.existsByLicenseNumber(ticketDto.getLicenseNumber());
        LocalDateTime timeLimit = ticketDto.getTimeOut().plusHours(1);

        if(isExisting){
            if(LocalDateTime.now().isBefore(timeLimit)){
                return true;
            }
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
    private BigDecimal computeDaily(LocalDateTime entryDate, LocalDateTime exitDate, BigDecimal rate){
        long amount_due;
        long flat_daily = 5000;

        long days = ChronoUnit.DAYS.between(entryDate, exitDate);
        long hours = ChronoUnit.HOURS.between(entryDate,exitDate)%(days*24);

        amount_due = (flat_daily * days) + (hours * rate.longValue());

        return new BigDecimal(amount_due);
    }

    // Compute for hourly rate
    private BigDecimal computeHourly(LocalDateTime entryDate, LocalDateTime exitDate, BigDecimal flat_rate, BigDecimal per_hour){
        long amount_due;
        long hours = ChronoUnit.HOURS.between(entryDate,exitDate)-3;

        amount_due = (hours * per_hour.longValue()) + flat_rate.longValue();

        return new BigDecimal(amount_due);
    }

}
