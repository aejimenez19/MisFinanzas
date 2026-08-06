import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl, ReactiveFormsModule } from '@angular/forms';

import { CurrencyInputDirective } from './currency-input.directive';

@Component({
  standalone: true,
  imports: [ReactiveFormsModule, CurrencyInputDirective],
  template: `<input [formControl]="control" appCurrencyInput />`
})
class HostComponent {
  readonly control = new FormControl<number | null>(null);
}

describe('CurrencyInputDirective', () => {
  let fixture: ComponentFixture<HostComponent>;
  let input: HTMLInputElement;
  let host: HostComponent;

  beforeEach(() => {
    fixture = TestBed.createComponent(HostComponent);
    fixture.detectChanges();
    input = fixture.nativeElement.querySelector('input');
    host = fixture.componentInstance;
  });

  function type(value: string): void {
    input.value = value;
    input.dispatchEvent(new Event('input'));
    fixture.detectChanges();
  }

  it('formats thousands with commas as the user types', () => {
    type('1234567.89');
    expect(input.value).toBe('1,234,567.89');
    expect(host.control.value).toBe(1234567.89);
  });

  it('limits decimals to two digits', () => {
    type('12.345');
    expect(input.value).toBe('12.34');
    expect(host.control.value).toBe(12.34);
  });

  it('writes a formatted value when the control is set externally', () => {
    host.control.setValue(1234.5);
    fixture.detectChanges();
    expect(input.value).toBe('1,234.5');
  });

  it('keeps the control null when empty', () => {
    type('');
    expect(host.control.value).toBeNull();
  });

  it('normalizes the value on blur', () => {
    type('9999');
    input.dispatchEvent(new Event('blur'));
    expect(input.value).toBe('9,999');
  });
});
