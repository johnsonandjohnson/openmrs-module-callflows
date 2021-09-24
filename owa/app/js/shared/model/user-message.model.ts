export default class UserMessage {
  title: string;
  position: string;
  type: string;
  text: string;
  dateString: string;
  titleColor?: string;

  constructor(title: string, message: string, date: Date = new Date(), titleColor?: string) {
    this.title = title,
    this.position = 'right',
    this.type = 'text',
    this.text = message,
    this.dateString = date.toLocaleTimeString()
    this.titleColor = titleColor
  }
}
