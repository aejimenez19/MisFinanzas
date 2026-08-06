import { ChangeDetectorRef, Directive, ElementRef, HostListener, forwardRef, inject } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';

function formatDigits(value: string): string {
  const digits = value.replace(/[^\d.]/g, '');
  const firstDot = digits.indexOf('.');
  let intRaw = digits;
  let decRaw: string | undefined;
  if (firstDot !== -1) {
    intRaw = digits.slice(0, firstDot);
    decRaw = digits.slice(firstDot + 1).replace(/\./g, '').slice(0, 2);
  }
  const intFormatted = intRaw.replace(/\B(?=(\d{3})+(?!\d))/g, ',');
  return decRaw === undefined ? intFormatted : `${intFormatted}.${decRaw}`;
}

function parseCurrency(value: string): number | null {
  const cleaned = value.replace(/,/g, '').trim();
  if (cleaned === '' || cleaned === '.') {
    return null;
  }
  const parsed = Number(cleaned);
  return Number.isFinite(parsed) ? parsed : null;
}

function formatValue(value: number | null | undefined): string {
  if (value == null || !Number.isFinite(value)) {
    return '';
  }
  return value.toLocaleString('en-US', { maximumFractionDigits: 2 });
}

@Directive({
  selector: 'input[appCurrencyInput]',
  standalone: true,
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => CurrencyInputDirective),
      multi: true
    }
  ]
})
export class CurrencyInputDirective implements ControlValueAccessor {
  private readonly el = inject(ElementRef<HTMLInputElement>);
  private readonly cdr = inject(ChangeDetectorRef);

  private onChange: (value: number | null) => void = () => undefined;
  private onTouched: () => void = () => undefined;

  @HostListener('input', ['$event'])
  onInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    const caret = input.selectionStart ?? input.value.length;
    const before = input.value.slice(0, caret);

    const formatted = formatDigits(input.value);
    if (formatted !== input.value) {
      const commasBefore = (before.match(/,/g) ?? []).length;
      const formattedBefore = formatDigits(before);
      const commasFormatted = (formattedBefore.match(/,/g) ?? []).length;
      input.value = formatted;
      input.setSelectionRange(caret + (commasFormatted - commasBefore), caret + (commasFormatted - commasBefore));
    }
    this.onChange(parseCurrency(formatted));
    this.cdr.markForCheck();
  }

  @HostListener('blur')
  onBlur(): void {
    const formatted = formatValue(parseCurrency(this.el.nativeElement.value));
    if (formatted !== this.el.nativeElement.value) {
      this.el.nativeElement.value = formatted;
    }
    this.onTouched();
  }

  writeValue(value: number | null | undefined): void {
    this.el.nativeElement.value = formatValue(value);
  }

  registerOnChange(fn: (value: number | null) => void): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: () => void): void {
    this.onTouched = fn;
  }

  setDisabledState(isDisabled: boolean): void {
    this.el.nativeElement.disabled = isDisabled;
  }
}
